package com.morales.calcontrol.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.morales.calcontrol.CalControlApp
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {

    /**
     * Verifica si el permiso de notificaciones está concedido (Android 13+)
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    /**
     * Programa la tarea de WorkManager para el recordatorio/resumen diario.
     * En este ejemplo, se configura a 10 segundos para pruebas.
     */
    fun scheduleDailyNotification() {
        val testWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .addTag("daily_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_reminder",
            ExistingWorkPolicy.REPLACE,
            testWorkRequest
        )
    }

    /**
     * Muestra la notificación de alerta inmediata cuando se supera la meta de calorías.
     */
    fun enviarAlertaMetaSuperada(total: Double) {
        val builder = NotificationCompat.Builder(context, CalControlApp.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("⚠️ Meta de calorías superada")
            .setContentText("Has consumido ${total.toInt()} kcal. Has superado tu límite diario de 2000 kcal.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(200, builder.build())
        } catch (e: SecurityException) {
            // Manejar falta de permisos si es necesario
        }
    }
}