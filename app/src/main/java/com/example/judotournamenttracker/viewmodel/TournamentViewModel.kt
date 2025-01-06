package com.example.judotournamenttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.data.TournamentDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TournamentViewModel(private val tournamentDao: TournamentDao) : ViewModel() {
    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments

    init {
        viewModelScope.launch {
            tournamentDao.getAllTournaments().collect { tournaments ->
                _tournaments.value = tournaments
            }
        }
    }

    fun addTournament(tournament: Tournament) {
        viewModelScope.launch {
            tournamentDao.insertTournament(tournament)
        }
    }
}