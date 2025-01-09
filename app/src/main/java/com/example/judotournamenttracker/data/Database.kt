package com.example.judotournamenttracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament)

    @Query("SELECT * FROM tournaments")
    fun getAllTournaments(): Flow<List<Tournament>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournamentCategoryCrossRef(crossRef: TournamentCategoryCrossRef)

    @Transaction
    @Query("SELECT * FROM tournaments WHERE tournament_id = :tournamentId")
    fun getTournamentWithCategories(tournamentId: Int): Flow<TournamentWithCategories>

    @Transaction
    @Query("SELECT * FROM categories WHERE category_id = :categoryId")
    fun getCategoryWithTournaments(categoryId: Int): Flow<CategoryWithTournaments>
}

@Database(
    entities = [Tournament::class, Category::class, TournamentCategoryCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
}