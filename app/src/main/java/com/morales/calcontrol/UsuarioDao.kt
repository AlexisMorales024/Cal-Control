package com.morales.calcontrol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query(
        "SELECT * FROM usuarios WHERE correo = :correo AND password = :password"
    )
    suspend fun login(
        correo: String,
        password: String
    ): Usuario?
}