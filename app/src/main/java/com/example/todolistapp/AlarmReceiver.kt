package com.example.todolistapp

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todolistapp.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Get task details from the intent
        val taskId = intent.getIntExtra("taskId", -1)
        val taskName = intent.getStringExtra("taskName")

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, "task_notifications")
            .setContentTitle("Task Reminder")
            .setContentText("It's time to do: $taskName")
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(taskId, notificationBuilder.build())
    }
}
