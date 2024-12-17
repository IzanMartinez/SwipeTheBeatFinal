package com.izamaralv.swipethebeat.ui.components

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.izamaralv.swipethebeat.MainActivity
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.softComponentColor

object NotificationHelper {

    private const val CHANNEL_ID = "persistent_channel"
    private const val CHANNEL_NAME = "Persistent Notifications"
    private const val CHANNEL_DESCRIPTION = "Channel for persistent notifications"
    private const val NOTIFICATION_ID = 1

    // Crear canal de notificaciones
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    // Mostrar notificación persistente
    fun showPersistentNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.border_logo_green)
            .setContentTitle("Swipe the beat")
            .setContentText("Toca aquí para volver a Swipe the beat")
            .setColor(softComponentColor.value.toArgb())
            .setPriority(NotificationCompat.PRIORITY_MAX) // Prioridad máxima para notificaciones emergentes
            .setCategory(NotificationCompat.CATEGORY_SERVICE) // Tratada como un servicio continuo
            .setOngoing(true) // Asegura que permanezca activa
            .setAutoCancel(false) // No se cancela al hacer clic
            .setContentIntent(pendingIntent) // Abre tu aplicación cuando se toca
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
