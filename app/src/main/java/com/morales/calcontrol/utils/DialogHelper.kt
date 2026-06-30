package com.morales.calcontrol.utils

import android.content.Context
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.morales.calcontrol.data.EntradaAlimento
import com.morales.calcontrol.viewmodel.EntradaAlimentoViewModel

class DialogHelper(private val context: Context) {

    /**
     * Muestra el diálogo de confirmación para eliminar un registro
     */
    fun mostrarDialogoEliminar(
        alimento: EntradaAlimento,
        viewModel: EntradaAlimentoViewModel,
        viewRoot: View
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Eliminar registro")
            .setMessage("¿Deseas eliminar '${alimento.nombre_alimento}'?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.delete(alimento)
                Snackbar.make(viewRoot, "Registro eliminado", Snackbar.LENGTH_LONG)
                    .setAction("Deshacer") { viewModel.insert(alimento) }
                    .show()
            }.show()
    }
}