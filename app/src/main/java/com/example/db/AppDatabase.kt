package com.example.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.model.SolutionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolutionDao {
    @Query("SELECT * FROM solution_history ORDER BY timestamp DESC")
    fun getAllSolutions(): Flow<List<SolutionEntity>>

    @Query("SELECT * FROM solution_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteSolutions(): Flow<List<SolutionEntity>>

    @Query("SELECT * FROM solution_history WHERE questionNormalized = :normalized LIMIT 1")
    suspend fun findByNormalized(normalized: String): SolutionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solution: SolutionEntity): Long

    @Update
    suspend fun update(solution: SolutionEntity)

    @Query("UPDATE solution_history SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)

    @Query("DELETE FROM solution_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM solution_history")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM solution_history")
    suspend fun getCount(): Int
}

@Database(entities = [SolutionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun solutionDao(): SolutionDao
}
