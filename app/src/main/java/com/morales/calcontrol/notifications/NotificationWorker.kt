package com.morales.calcontrol.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.morales.calcontrol.CalControlApp
import com.morales.calcontrol.data.AppDatabase
import kotlinx.coroutines.flow.first
import java.util.*

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).entradaAlimentoDao()
        val todasLasEntradas = dao.getAll().first()

        if (todasLasEntradas.isEmpty()) {
            enviarNotificacion(
                "¡No has registrado nada hoy!",
                "Mantén tu hábito. Registra tus alimentos para no perder el control de tus calorías.",
                101
            )
        } else {
            val totalCalorias = todasLasEntradas.sumOf { it.calorias_calculadas }
            enviarNotificacion(
                "Resumen de tu día",
                "Has consumido un total de ${totalCalorias.toInt()} kcal. ¡Buen trabajo!",
                102
            )
        }

        return Result.success()
    }

    private fun enviarNotificacion(titulo: String, mensaje: String, id: Int) {
        val builder = NotificationCompat.Builder(applicationContext, CalControlApp.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(applicationContext).notify(id, builder.build())
        } catch (e: SecurityException) { }
    }
}