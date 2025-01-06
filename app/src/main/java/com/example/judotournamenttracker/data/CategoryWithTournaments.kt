package com.example.judotournamenttracker.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CategoryWithTournaments(
    @Embedded val category: Category, // Základní informace o kategorii
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TournamentCategoryCrossRef::class,
            parentColumn = "categoryId",
            entityColumn = "tournamentId"
        )
    )
    val tournaments: List<Tournament> // Seznam turnajů spojených s kategorií
)