package com.bignerdranch.android.scores

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

//Source: https://stackoverflow.com/questions/56106112/android-8-9-notification-scheduling
class NotificationSchedule (var context: Context, var params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val data = params.inputData
        val message = data.getString("message")

        var builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker("Game starting soon!")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(message)
            .setContentIntent(PendingIntent.getActivity(context, 0, ScoresActivity.newIntent(context), 0))
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(0, builder.build())

        return Result.success()
    }
}