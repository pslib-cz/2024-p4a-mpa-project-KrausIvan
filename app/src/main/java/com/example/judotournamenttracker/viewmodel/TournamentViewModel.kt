package com.example.judotournamenttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.judotournamenttracker.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TournamentViewModel(private val tournamentDao: TournamentDao) : ViewModel() {

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val filteredTournaments: StateFlow<List<Tournament>> = combine(
        _tournaments, _selectedCategoryId
    ) { tournaments, categoryId ->
        if (categoryId == null) {
            tournaments
        } else {
            tournaments.filter { tournament ->
                val tournamentWithCategories = tournamentDao.getTournamentWithCategories(tournament.id)
                tournamentWithCategories.firstOrNull()?.categories?.any { it.id == categoryId } == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            tournamentDao.getAllTournaments().collect { tournaments ->
                _tournaments.value = tournaments
            }

            tournamentDao.getAllCategories().collect { categories ->
                _categories.value = categories
            }
        }
    }

    fun addTournament(tournament: Tournament) {
        viewModelScope.launch {
            tournamentDao.insertTournament(tournament)
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            tournamentDao.insertCategory(category)
        }
    }

    fun addTournamentToCategory(tournamentId: Int, categoryId: Int) {
        viewModelScope.launch {
            tournamentDao.insertTournamentCategoryCrossRef(
                TournamentCategoryCrossRef(tournamentId, categoryId)
            )
        }
    }

    fun setSelectedCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun exportTournaments(): String {
        return Json.encodeToString(_tournaments.value)
    }
}