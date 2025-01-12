package com.example.judotournamenttracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tournament_id")
    val id: Int = 0,
    val name: String,
    val location: String,
    val date: String,
    val description: String
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "tournament_category_cross_ref",
    primaryKeys = ["tournament_id", "category_id"]
)
data class TournamentCategoryCrossRef(
    @ColumnInfo(name = "tournament_id")
    val tournamentId: Int,
    @ColumnInfo(name = "category_id")
    val categoryId: Int
)