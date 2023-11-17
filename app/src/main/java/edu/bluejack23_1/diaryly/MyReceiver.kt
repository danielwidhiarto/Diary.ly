package edu.bluejack23_1.diaryly

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }
    private fun showNotification(context: Context) {
        val channelId = "your_channel_id"
        val notificationId = 1

        // Create a NotificationCompat.Builder instance
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app logo
            .setContentTitle("Moods & Journal Reminder")
            .setContentText("Don't forget to input your journal and moods today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Get the NotificationManagerCompat
        val notificationManager = NotificationManagerCompat.from(context)

        // Check for the VIBRATE permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.VIBRATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Notify if permission is granted
            notificationManager.notify(notificationId, builder.build())
        } else {
            // If permission is not granted, you might want to request it here
            // and handle the result in the onRequestPermissionsResult callback
            // or show a rationale for why the permission is needed.
        }
    }
}
