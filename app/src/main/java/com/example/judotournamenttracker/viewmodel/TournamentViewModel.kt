package com.example.judotournamenttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.judotournamenttracker.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TournamentViewModel(private val tournamentDao: TournamentDao) : ViewModel() {

    private val _allTournamentsWithCats = MutableStateFlow<List<TournamentWithCategories>>(emptyList())
    val allTournamentsWithCats: StateFlow<List<TournamentWithCategories>> = _allTournamentsWithCats

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    private val _searchText = MutableStateFlow("")
    private val _sortAscending = MutableStateFlow(true)

    val searchText: StateFlow<String> = _searchText
    val sortAscending: StateFlow<Boolean> = _sortAscending

    init {
        viewModelScope.launch {
            tournamentDao.getAllTournamentsWithCategories().collect { list ->
                _allTournamentsWithCats.value = list
            }
        }
        viewModelScope.launch {
            tournamentDao.getAllCategories().collect { list ->
                _categories.value = list
            }
        }
    }

    val filteredTournamentsWithCats: StateFlow<List<TournamentWithCategories>> =
        combine(
            allTournamentsWithCats,
            _selectedCategoryId,
            _searchText,
            _sortAscending
        ) { all, catId, text, asc ->
            val catFiltered = if (catId == null) {
                all
            } else {
                all.filter { twc ->
                    twc.categories.any { it.id == catId }
                }
            }
            val searchFiltered = if (text.isBlank()) {
                catFiltered
            } else {
                catFiltered.filter {
                    it.tournament.name.contains(text, ignoreCase = true)
                }
            }
            if (asc) {
                searchFiltered.sortedBy { it.tournament.date }
            } else {
                searchFiltered.sortedByDescending { it.tournament.date }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTournamentMulti(tournament: Tournament, categoryIds: Set<Int>) {
        viewModelScope.launch {
            val newId = tournamentDao.insertTournamentReturnId(tournament)
            categoryIds.forEach { catId ->
                tournamentDao.insertTournamentCategoryCrossRef(
                    TournamentCategoryCrossRef(newId.toInt(), catId)
                )
            }
        }
    }

    fun updateTournamentMulti(tournament: Tournament, newCategoryIds: Set<Int>) {
        viewModelScope.launch {
            tournamentDao.updateTournament(tournament)
            val oldCats = tournamentDao
                .getTournamentWithCategories(tournament.id)
                .firstOrNull()
                ?.categories
                ?: emptyList()
            oldCats.forEach { oldCat ->
                tournamentDao.deleteTournamentCategoryCrossRef(
                    TournamentCategoryCrossRef(tournament.id, oldCat.id)
                )
            }
            newCategoryIds.forEach { catId ->
                tournamentDao.insertTournamentCategoryCrossRef(
                    TournamentCategoryCrossRef(tournament.id, catId)
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

    fun loadTournamentCategoriesOnce(tournamentId: Int): TournamentWithCategories? {
        return runBlocking {
            tournamentDao.getTournamentWithCategories(tournamentId)
                .firstOrNull()
        }
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
        val justTournaments = _allTournamentsWithCats.value.map { it.tournament }
        return Json.encodeToString(justTournaments)
    }
}