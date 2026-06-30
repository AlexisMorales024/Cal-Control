package com.morales.calcontrol.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.morales.calcontrol.databinding.LayoutAgregarAlimentoBinding
import com.morales.calcontrol.data.EntradaAlimento

class AgregarAlimentoDialogFragment : BottomSheetDialogFragment() {

    private var _binding: LayoutAgregarAlimentoBinding? = null
    private val binding get() = _binding!!
    
    private var onGuardar: ((EntradaAlimento) -> Unit)? = null
    private var alimentoAEditar: EntradaAlimento? = null

    companion object {
        private const val ARG_ALIMENTO = "arg_alimento"

        fun newInstance(alimento: EntradaAlimento? = null, onGuardar: (EntradaAlimento) -> Unit): AgregarAlimentoDialogFragment {
            val fragment = AgregarAlimentoDialogFragment()
            fragment.onGuardar = onGuardar
            alimento?.let {
                val args = Bundle()
                args.putParcelable(ARG_ALIMENTO, it)
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutAgregarAlimentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar alimento si estamos editando
        alimentoAEditar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_ALIMENTO, EntradaAlimento::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_ALIMENTO)
        }

        // Si hay un alimento, pre-poblar los campos
        alimentoAEditar?.let {
            binding.editNombreAlimento.setText(it.nombre_alimento)
            binding.editTipoComida.setText(it.tipo_comida)
            binding.editCantidad.setText(it.cantidad.toString())
            binding.editHoraConsumo.setText(it.hora_consumo)
            binding.editCaloriasCalculadas.setText(it.calorias_calculadas.toString())
            binding.editProteinasCalculadas.setText(it.proteinas_calculadas.toString())
            binding.editCarbohidratosCalculados.setText(it.carbohidratos_calculados.toString())
            binding.editGrasasCalculadas.setText(it.grasas_calculadas.toString())
            
            binding.btnGuardar.text = "Actualizar Registro"
        }

        binding.btnGuardar.setOnClickListener {
            val nombreAlimento = binding.editNombreAlimento.text.toString()
            val tipoComida = binding.editTipoComida.text.toString()
            val cantidad = binding.editCantidad.text.toString().toDoubleOrNull() ?: 0.0
            val hora = binding.editHoraConsumo.text.toString()
            val calorias = binding.editCaloriasCalculadas.text.toString().toDoubleOrNull() ?: 0.0
            val proteinas = binding.editProteinasCalculadas.text.toString().toDoubleOrNull() ?: 0.0
            val carbohidratos = binding.editCarbohidratosCalculados.text.toString().toDoubleOrNull() ?: 0.0
            val grasas = binding.editGrasasCalculadas.text.toString().toDoubleOrNull() ?: 0.0

            if (nombreAlimento.isNotEmpty() && tipoComida.isNotEmpty()) {
                val alimentoFinal = EntradaAlimento(
                    entrada_id = alimentoAEditar?.entrada_id ?: 0, 
                    registro_id = alimentoAEditar?.registro_id ?: 0,
                    alimento_id = alimentoAEditar?.alimento_id ?: 0,
                    nombre_alimento = nombreAlimento,
                    cantidad = cantidad,
                    tipo_comida = tipoComida,
                    calorias_calculadas = calorias,
                    proteinas_calculadas = proteinas,
                    carbohidratos_calculados = carbohidratos,
                    grasas_calculadas = grasas,
                    hora_consumo = hora
                )
                onGuardar?.invoke(alimentoFinal)
                dismiss()
            } else {
                if (nombreAlimento.isEmpty()) binding.editNombreAlimento.error = "Campo obligatorio"
                if (tipoComida.isEmpty()) binding.editTipoComida.error = "Campo obligatorio"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}