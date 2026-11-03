package com.example.dz6.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        NotificationHelper.showReminder(context, name)
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }
}
