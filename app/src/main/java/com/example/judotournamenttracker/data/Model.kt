package com.example.judotournamenttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val location: String,
    val date: String,
    val description: String
)

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(primaryKeys = ["tournamentId", "categoryId"])
data class TournamentCategoryCrossRef(
    val tournamentId: Int,
    val categoryId: Int
)