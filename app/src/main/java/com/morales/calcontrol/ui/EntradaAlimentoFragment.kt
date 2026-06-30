package com.morales.calcontrol.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.morales.calcontrol.R
import com.morales.calcontrol.data.AppDatabase
import com.morales.calcontrol.data.EntradaAlimento
import com.morales.calcontrol.repository.EntradaAlimentoRepository
import com.morales.calcontrol.viewmodel.EntradaAlimentoViewModel
import com.morales.calcontrol.viewmodel.EntradaAlimentoViewModelFactory

class EntradaAlimentoFragment : Fragment(R.layout.fragment_entrada_alimento) {

    private lateinit var viewModel: EntradaAlimentoViewModel
    private lateinit var adapter: EntradaAlimentoAdapter
    private var itemParaEditar: EntradaAlimento? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 Inicializar ViewModel
        val dao = AppDatabase.getDatabase(requireContext()).entradaAlimentoDao()
        val repository = EntradaAlimentoRepository(dao)
        viewModel = ViewModelProvider(this, EntradaAlimentoViewModelFactory(repository))[EntradaAlimentoViewModel::class.java]

        // 🔹 Conectar Vistas
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        
        val edtNombreAlimento = view.findViewById<EditText>(R.id.edtNombreAlimento)
        val edtTipoComida = view.findViewById<EditText>(R.id.edtTipoComida)
        val edtCantidad = view.findViewById<EditText>(R.id.edtCantidad)
        val edtHoraConsumo = view.findViewById<EditText>(R.id.edtHoraConsumo)
        val edtCalorias = view.findViewById<EditText>(R.id.edtCalorias)
        val edtProteinas = view.findViewById<EditText>(R.id.edtProteinas)
        val edtCarbohidratos = view.findViewById<EditText>(R.id.edtCarbohidratos)
        val edtGrasa = view.findViewById<EditText>(R.id.edtGrasa)

        // 🔹 Configurar Adapter
        adapter = EntradaAlimentoAdapter(
            onItemClick = { alimento ->
                itemParaEditar = alimento
                edtNombreAlimento.setText(alimento.nombre_alimento)
                edtTipoComida.setText(alimento.tipo_comida)
                edtCantidad.setText(alimento.cantidad.toString())
                edtHoraConsumo.setText(alimento.hora_consumo)
                edtCalorias.setText(alimento.calorias_calculadas.toString())
                edtProteinas.setText(alimento.proteinas_calculadas.toString())
                edtCarbohidratos.setText(alimento.carbohidratos_calculados.toString())
                edtGrasa.setText(alimento.grasas_calculadas.toString())
                btnGuardar.text = "Actualizar"
            },
            onItemLongClick = { alimento ->
                mostrarDialogoConfirmacion(alimento)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 🔹 Observar Datos
        viewModel.listaAlimentos.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

        // 🔹 Lógica del Botón Guardar / Actualizar
        btnGuardar.setOnClickListener {
            val nombreAlimento = edtNombreAlimento.text.toString()
            val tipoComida = edtTipoComida.text.toString()
            val cantidad = edtCantidad.text.toString().toDoubleOrNull() ?: 0.0
            val hora = edtHoraConsumo.text.toString()
            val calorias = edtCalorias.text.toString().toDoubleOrNull() ?: 0.0
            val proteinas = edtProteinas.text.toString().toDoubleOrNull() ?: 0.0
            val carbohidratos = edtCarbohidratos.text.toString().toDoubleOrNull() ?: 0.0
            val grasa = edtGrasa.text.toString().toDoubleOrNull() ?: 0.0

            if (nombreAlimento.isNotEmpty() && tipoComida.isNotEmpty()) {
                if (itemParaEditar == null) {
                    val nuevo = EntradaAlimento(
                        nombre_alimento = nombreAlimento,
                        tipo_comida = tipoComida,
                        cantidad = cantidad,
                        hora_consumo = hora,
                        calorias_calculadas = calorias,
                        proteinas_calculadas = proteinas,
                        carbohidratos_calculados = carbohidratos,
                        grasas_calculadas = grasa
                    )
                    viewModel.insert(nuevo)
                    Snackbar.make(view, "Guardado correctamente", Snackbar.LENGTH_SHORT).show()
                } else {
                    val actualizado = itemParaEditar!!.copy(
                        nombre_alimento = nombreAlimento,
                        tipo_comida = tipoComida,
                        cantidad = cantidad,
                        hora_consumo = hora,
                        calorias_calculadas = calorias,
                        proteinas_calculadas = proteinas,
                        carbohidratos_calculados = carbohidratos,
                        grasas_calculadas = grasa
                    )
                    viewModel.update(actualizado)
                    Snackbar.make(view, "Actualizado correctamente", Snackbar.LENGTH_SHORT).show()

                    itemParaEditar = null
                    btnGuardar.text = "Guardar"
                }
                limpiarCampos(edtNombreAlimento, edtTipoComida, edtCantidad, edtHoraConsumo, edtCalorias, edtProteinas, edtCarbohidratos, edtGrasa)
            } else {
                if (nombreAlimento.isEmpty()) edtNombreAlimento.error = "Campo obligatorio"
                if (tipoComida.isEmpty()) edtTipoComida.error = "Campo obligatorio"
            }
        }
    }

    private fun limpiarCampos(vararg editTexts: EditText) {
        editTexts.forEach { it.text.clear() }
    }

    private fun mostrarDialogoConfirmacion(alimento: EntradaAlimento) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar registro")
            .setMessage("¿Estás seguro de que deseas eliminar este registro de '${alimento.nombre_alimento}'?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.delete(alimento)
                view?.let {
                    Snackbar.make(it, "${alimento.nombre_alimento} eliminado", Snackbar.LENGTH_LONG)
                        .setAction("Deshacer") {
                            viewModel.insert(alimento)
                        }
                        .show()
                }
            }
            .show()
    }
}