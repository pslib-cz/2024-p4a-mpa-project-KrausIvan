package com.example.judotournamenttracker.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TournamentWithCategories(
    @Embedded val tournament: Tournament, // Základní informace o turnaji
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TournamentCategoryCrossRef::class,
            parentColumn = "tournamentId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<Category> // Seznam kategorií spojených s turnajem
)