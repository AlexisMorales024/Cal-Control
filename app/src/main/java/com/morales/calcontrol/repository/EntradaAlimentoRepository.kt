package com.morales.calcontrol.repository

import com.morales.calcontrol.data.EntradaAlimento
import com.morales.calcontrol.data.EntradaAlimentoDao

class EntradaAlimentoRepository(
    private val dao: EntradaAlimentoDao
) {

    val lista = dao.getAll()

    suspend fun insert(entrada: EntradaAlimento) {
        dao.insert(entrada)
    }

    suspend fun update(entrada: EntradaAlimento) {
        dao.update(entrada)
    }

    suspend fun delete(entrada: EntradaAlimento) {
        dao.delete(entrada)
    }
}