package com.bignerdranch.android.scores

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "ScoresActivity"

class ScoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        val currentFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = ScoresFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
        }
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, ScoresActivity::class.java)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun createNotification(gameEvent: GameEvent, calendar: Calendar) {
            val data = Data.Builder().putString("message", "${gameEvent.shortName} is starting soon!")

            //time difference between game start and current time, set notification to 5 minutes before (300000 ms)
            val diff = calendar.timeInMillis - Calendar.getInstance().timeInMillis - 300000
            //Log.d(TAG, "notif scheduled in $diff ms")

            //Source: https://stackoverflow.com/questions/56106112/android-8-9-notification-scheduling
            val work = OneTimeWorkRequestBuilder<NotificationSchedule>()
                    .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                    .setConstraints(Constraints.Builder().setTriggerContentMaxDelay(1, TimeUnit.SECONDS).build()) // API Level 24
                    .setInputData(data.build())
                    .addTag("Scheduled notification")
                    .build()

            WorkManager.getInstance().enqueue(work)
        }
    }

}