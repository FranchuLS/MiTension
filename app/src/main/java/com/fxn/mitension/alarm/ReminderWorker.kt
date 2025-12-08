package com.fxn.mitension.alarm

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fxn.mitension.MainActivity
import com.fxn.mitension.R
import com.fxn.mitension.data.AppDatabase
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.util.obtenerPeriodoActual
import com.fxn.mitension.util.obtenerRangoTimestamps

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val CHANNEL_ID = "TENSION_REMINDERS"

    override suspend fun doWork(): Result {
        // Obtenemos el período y rango de tiempo actual y accedemos a la BD
        val periodoActual = obtenerPeriodoActual()
        val (inicio, fin) = obtenerRangoTimestamps(periodoActual)
        val dao = AppDatabase.getDatabase(context).medicionDao()
        val repository = MedicionRepository(dao)
        val conteo = repository.contarMedicionesEnRango(inicio, fin)

        //Comprobaremos si se debe enviar una notificación
        if (conteo < 3) {
            val registrosFaltantes = 3 - conteo
            val nombrePeriodo = context.getString(
                when (periodoActual) {
                    com.fxn.mitension.util.PeriodoDelDia.MAÑANA -> R.string.periodo_manana
                    com.fxn.mitension.util.PeriodoDelDia.TARDE -> R.string.periodo_tarde
                    com.fxn.mitension.util.PeriodoDelDia.NOCHE -> R.string.periodo_noche
                }
            )

            val titulo = context.getString(R.string.notificacion_titulo)
            val texto =
                context.getString(R.string.notificacion_texto, registrosFaltantes, nombrePeriodo)

            enviarNotificacion(titulo, texto)
        }

        return Result.success()
    }

    private fun enviarNotificacion(titulo: String, texto: String) {
        // Creamos un Intent explícito que apunta a nuestra MainActivity.
        val intent = Intent(context, MainActivity::class.java).apply {
            // Flags para asegurar que se abra correctamente o se traiga al frente si ya está abierta.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Envolvemos nuestro Intent en un PendingIntent. Le damos al sistema permiso para ejecutarlo.
        // TaskStackBuilder asegura que al pulsar "atrás" desde la app, no volvamos a una pantalla vacía.
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Añade la pila de "atrás" para el Intent (en este caso, solo la MainActivity)
            addNextIntentWithParentStack(intent)
            // Obtenemos el PendingIntent. El flag 'FLAG_IMMUTABLE' es obligatorio y una buena práctica de seguridad.
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        // Construimos la notificación
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.mi_tension_alerta_24)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Comprobamos si tenemos permiso para enviar notificaciones (necesario en API 33+)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Mostramos la notificación
        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build()) // El ID '1' es para esta notificación
        }
    }
}
    