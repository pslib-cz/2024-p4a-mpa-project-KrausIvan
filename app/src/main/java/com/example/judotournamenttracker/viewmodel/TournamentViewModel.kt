package com.example.judotournamenttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.judotournamenttracker.data.Category
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.data.TournamentCategoryCrossRef
import com.example.judotournamenttracker.data.TournamentDao
import com.example.judotournamenttracker.data.TournamentWithCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TournamentViewModel(private val tournamentDao: TournamentDao) : ViewModel() {

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    private val _searchText = MutableStateFlow("")
    private val _sortAscending = MutableStateFlow(true)

    val searchText: StateFlow<String> = _searchText
    val sortAscending: StateFlow<Boolean> = _sortAscending

    init {
        viewModelScope.launch {
            tournamentDao.getAllTournaments().collect { list ->
                _tournaments.value = list
            }
        }
        viewModelScope.launch {
            tournamentDao.getAllCategories().collect { list ->
                _categories.value = list
            }
        }
    }

    val filteredTournaments: StateFlow<List<Tournament>> =
        combine(_tournaments, _selectedCategoryId, _searchText, _sortAscending)
        { allTournaments, categoryId, text, asc ->
            val catFiltered = if (categoryId == null) {
                allTournaments
            } else {
                allTournaments.filter { t ->
                    val flow = tournamentDao.getTournamentWithCategories(t.id)
                    runBlocking {
                        val twc = flow.firstOrNull()
                        twc?.categories?.any { it.id == categoryId } ?: false
                    }
                }
            }
            val searchFiltered = if (text.isBlank()) {
                catFiltered
            } else {
                catFiltered.filter { it.name.contains(text, ignoreCase = true) }
            }
            if (asc) {
                searchFiltered.sortedBy { it.date }
            } else {
                searchFiltered.sortedByDescending { it.date }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTournament(tournament: Tournament, categoryId: Int?) {
        viewModelScope.launch {
            val newId = tournamentDao.insertTournamentReturnId(tournament)
            if (categoryId != null) {
                tournamentDao.insertTournamentCategoryCrossRef(
                    TournamentCategoryCrossRef(newId.toInt(), categoryId)
                )
            }
        }
    }

    fun updateTournament(tournament: Tournament, newCategoryId: Int?, oldCategoryId: Int?) {
        viewModelScope.launch {
            tournamentDao.updateTournament(tournament)
            if (oldCategoryId != null) {
                tournamentDao.deleteTournamentCategoryCrossRef(
                    TournamentCategoryCrossRef(tournament.id, oldCategoryId)
                )
            }
            if (newCategoryId != null) {
                tournamentDao.insertTournamentCategoryCrossRef(
                    TournamentCategoryCrossRef(tournament.id, newCategoryId)
                )
            }
        }
    }

    fun deleteTournament(tournament: Tournament) {
        viewModelScope.launch {
            tournamentDao.deleteTournament(tournament)
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            tournamentDao.insertCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            tournamentDao.deleteCategory(category)
        }
    }

    fun getTournamentCategoriesFlow(tournamentId: Int): StateFlow<TournamentWithCategories?> {
        return tournamentDao.getTournamentWithCategories(tournamentId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun setSelectedCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }

    fun exportTournaments(): String {
        return Json.encodeToString(_tournaments.value)
    }

    fun isTournamentInCategory(tournament: Tournament, category: Category): Boolean {
        return runBlocking {
            val twc = tournamentDao
                .getTournamentWithCategories(tournament.id)
                .firstOrNull()
            twc?.categories?.any { it.id == category.id } ?: false
        }
    }
}