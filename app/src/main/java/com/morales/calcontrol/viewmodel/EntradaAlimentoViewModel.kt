package com.morales.calcontrol.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.morales.calcontrol.data.AppDatabase
import com.morales.calcontrol.data.EntradaAlimento
import com.morales.calcontrol.data.EntradaAlimentoDao
import com.morales.calcontrol.repository.EntradaAlimentoRepository
import kotlinx.coroutines.launch

class EntradaAlimentoViewModel(
    private val repository: EntradaAlimentoRepository
) : ViewModel() {

    val listaAlimentos = repository.lista.asLiveData()

    fun insert(entrada: EntradaAlimento) {
        viewModelScope.launch {
            repository.insert(entrada)
        }
    }

    fun update(entrada: EntradaAlimento) {
        viewModelScope.launch {
            repository.update(entrada)
        }
    }

    fun delete(entrada: EntradaAlimento) {
        viewModelScope.launch {
            repository.delete(entrada)
        }
    }
}