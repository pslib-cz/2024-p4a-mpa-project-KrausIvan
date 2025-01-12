package com.example.judotournamenttracker.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CategoryWithTournaments(
    @Embedded val category: Category,

    @Relation(
        parentColumn = "category_id",
        entityColumn = "tournament_id",
        associateBy = Junction(
            value = TournamentCategoryCrossRef::class,
            parentColumn = "category_id",
            entityColumn = "tournament_id"
        )
    )
    val tournaments: List<Tournament>
)