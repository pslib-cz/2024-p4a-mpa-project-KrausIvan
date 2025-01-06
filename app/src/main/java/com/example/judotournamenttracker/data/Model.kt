package com.example.judotournamenttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabulka pro turnaje
@Entity
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primární klíč s automatickým generováním
    val name: String, // Název turnaje
    val location: String, // Lokace turnaje
    val date: String, // Datum turnaje
    val description: String // Popis turnaje
)

// Tabulka pro kategorie
@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primární klíč s automatickým generováním
    val name: String // Název kategorie
)

// Vazební tabulka pro relaci N:M mezi turnaji a kategoriemi
@Entity(primaryKeys = ["tournamentId", "categoryId"])
data class TournamentCategoryCrossRef(
    val tournamentId: Int, // ID turnaje
    val categoryId: Int // ID kategorie
)