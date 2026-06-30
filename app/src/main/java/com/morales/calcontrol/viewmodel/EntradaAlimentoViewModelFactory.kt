package com.morales.calcontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.morales.calcontrol.repository.EntradaAlimentoRepository

class EntradaAlimentoViewModelFactory(
    private val repository: EntradaAlimentoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EntradaAlimentoViewModel(repository) as T
    }
}