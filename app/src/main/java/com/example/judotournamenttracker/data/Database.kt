package com.example.judotournamenttracker.data

import androidx.room.*
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament)

    @Query("SELECT * FROM Tournament")
    fun getAllTournaments(): Flow<List<Tournament>>
}

@Database(
    entities = [Tournament::class, Category::class, TournamentCategoryCrossRef::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
}