package com.morales.calcontrol.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "EntradaAlimento")
data class EntradaAlimento(
    @PrimaryKey(autoGenerate = true)
    val entrada_id: Int = 0,
    val registro_id: Int = 0,
    val alimento_id: Int = 0,
    val nombre_alimento: String,
    val cantidad: Double,
    val tipo_comida: String,
    val calorias_calculadas: Double,
    val proteinas_calculadas: Double,
    val carbohidratos_calculados: Double,
    val grasas_calculadas: Double,
    val hora_consumo: String
) : Parcelable