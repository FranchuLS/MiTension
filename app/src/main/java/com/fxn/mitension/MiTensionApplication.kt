package com.fxn.mitension

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fxn.mitension.alarm.ReminderWorker
import java.util.concurrent.TimeUnit

class MiTensionApplication : Application() {
    private val CHANNEL_ID = "TENSION_REMINDERS"

    override fun onCreate() {
        super.onCreate()

        // Estas dos funciones se ejecutarán una sola vez cuando la app se inicie
        crearCanalDeNotificaciones()
        planificarTrabajoPeriodico()
    }

    private fun crearCanalDeNotificaciones() {
        val name = getString(R.string.canal_notificaciones_nombre)
        val descriptionText = getString(R.string.canal_notificaciones_descripcion)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Registramos el canal en el sistema
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun planificarTrabajoPeriodico() {
        // --- CONFIGURACIÓN DE LA ALARMA ---
        // Aquí configuramos los intervalos de repetición.
        // WorkManager tiene un mínimo de 15 minutos para trabajos periódicos.
        val intervaloRepeticion: Long = 2
        val unidadTiempo: TimeUnit = TimeUnit.HOURS

        // Creamos la petición de trabajo periódico que ejecutará nuestro ReminderWorker
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            intervaloRepeticion,
            unidadTiempo
        ).build()

        // Planificamos el trabajo con WorkManager, asegurándonos de que no se duplique
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RecordatorioTension", // Un nombre único para identificar este trabajo
            ExistingPeriodicWorkPolicy.KEEP, // Si ya existe, no lo reemplaces y déjalo como está
            workRequest
        )
    }
}
