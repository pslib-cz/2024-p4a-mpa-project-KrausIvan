package com.example.judotournamenttracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament)

    @Update
    suspend fun updateTournament(tournament: Tournament)

    @Delete
    suspend fun deleteTournament(tournament: Tournament)

    @Query("SELECT * FROM tournaments")
    fun getAllTournaments(): Flow<List<Tournament>>

    @Transaction
    @Query("SELECT * FROM tournaments WHERE tournament_id = :tournamentId")
    fun getTournamentWithCategories(tournamentId: Int): Flow<TournamentWithCategories>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories WHERE category_id = :categoryId")
    fun getCategoryWithTournaments(categoryId: Int): Flow<CategoryWithTournaments>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournamentCategoryCrossRef(crossRef: TournamentCategoryCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournamentReturnId(tournament: Tournament): Long

    @Delete
    suspend fun deleteTournamentCategoryCrossRef(crossRef: TournamentCategoryCrossRef)

    @Query("SELECT COUNT(*) FROM tournaments")
    suspend fun getAllTournamentsCount(): Int

}

@Database(
    entities = [
        Tournament::class,
        Category::class,
        TournamentCategoryCrossRef::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
}