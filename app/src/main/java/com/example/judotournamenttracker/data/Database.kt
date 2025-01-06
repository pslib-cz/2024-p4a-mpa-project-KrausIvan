package com.example.judotournamenttracker.data

import androidx.room.*
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {
    // Operace pro turnaje
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament)

    @Query("SELECT * FROM Tournament")
    fun getAllTournaments(): Flow<List<Tournament>>

    // Operace pro kategorie
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM Category")
    fun getAllCategories(): Flow<List<Category>>

    // Operace pro vazby turnajů a kategorií
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournamentCategoryCrossRef(crossRef: TournamentCategoryCrossRef)

    @Transaction
    @Query("SELECT * FROM Tournament WHERE id = :tournamentId")
    fun getTournamentWithCategories(tournamentId: Int): Flow<List<TournamentWithCategories>>

    @Transaction
    @Query("SELECT * FROM Category WHERE id = :categoryId")
    fun getCategoryWithTournaments(categoryId: Int): Flow<List<CategoryWithTournaments>>
}

// Definice databáze
@Database(
    entities = [Tournament::class, Category::class, TournamentCategoryCrossRef::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
}