package com.example.judotournamenttracker.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TournamentWithCategories(
    @Embedded val tournament: Tournament,
    @Relation(
        parentColumn = "tournament_id",
        entityColumn = "category_id",
        associateBy = Junction(
            value = TournamentCategoryCrossRef::class,
            parentColumn = "tournament_id",
            entityColumn = "category_id"
        )
    )
    val categories: List<Category>
)