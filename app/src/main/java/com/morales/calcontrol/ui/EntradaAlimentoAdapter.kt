package com.morales.calcontrol.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.morales.calcontrol.R
import com.morales.calcontrol.data.EntradaAlimento

class EntradaAlimentoAdapter(
    private val onItemClick: (EntradaAlimento) -> Unit,
    private val onItemLongClick: (EntradaAlimento) -> Unit
) : ListAdapter<EntradaAlimento, EntradaAlimentoAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtDetalles: TextView = itemView.findViewById(R.id.txtDetalles)
        val txtCaloriasItem: TextView = itemView.findViewById(R.id.txtCaloriasItem)
        val txtProtItem: TextView = itemView.findViewById(R.id.txtProtItem)
        val txtCarbItem: TextView = itemView.findViewById(R.id.txtCarbItem)
        val txtGrasaItem: TextView = itemView.findViewById(R.id.txtGrasaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        // Mostramos el nombre del alimento y el tipo de comida
        holder.txtNombre.text = "${item.nombre_alimento} (${item.tipo_comida})"
        holder.txtDetalles.text = "Cant: ${item.cantidad}g - ${item.hora_consumo}"
        holder.txtCaloriasItem.text = "${item.calorias_calculadas} kcal"
        
        holder.txtProtItem.text = "P: ${item.proteinas_calculadas}g"
        holder.txtCarbItem.text = "C: ${item.carbohidratos_calculados}g"
        holder.txtGrasaItem.text = "G: ${item.grasas_calculadas}g"

        // Click corto para editar
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Click largo para eliminar
        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EntradaAlimento>() {
        override fun areItemsTheSame(oldItem: EntradaAlimento, newItem: EntradaAlimento): Boolean {
            return oldItem.entrada_id == newItem.entrada_id
        }

        override fun areContentsTheSame(oldItem: EntradaAlimento, newItem: EntradaAlimento): Boolean {
            return oldItem == newItem
        }
    }
}
