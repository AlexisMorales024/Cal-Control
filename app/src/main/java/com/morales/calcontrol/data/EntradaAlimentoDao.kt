package com.morales.calcontrol.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradaAlimentoDao {

    @Insert
    suspend fun insert(entrada: EntradaAlimento)

    @Query("SELECT * FROM EntradaAlimento ORDER BY entrada_id DESC")
    fun getAll(): Flow<List<EntradaAlimento>>

    @Update
    suspend fun update(entrada: EntradaAlimento)

    @Delete
    suspend fun delete(entrada: EntradaAlimento)
}