package com.example.judotournamenttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tournament_id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "description") val description: String
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
)

@Entity(
    tableName = "tournament_category_cross_ref",
    primaryKeys = ["tournament_id", "category_id"]
)
data class TournamentCategoryCrossRef(
    @ColumnInfo(name = "tournament_id") val tournamentId: Int,
    @ColumnInfo(name = "category_id") val categoryId: Int,
)