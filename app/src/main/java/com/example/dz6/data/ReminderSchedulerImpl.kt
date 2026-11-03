package com.example.dz6.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.dz6.domain.model.ReminderTime
import com.example.dz6.domain.repository.ReminderScheduler
import java.util.Calendar

class ReminderSchedulerImpl(private val context: Context) : ReminderScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(time: ReminderTime, name: String) {
        NotificationHelper.ensureChannel(context)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Время уже прошло — переносим на завтра, иначе пуш сработает мгновенно
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        val triggerAt = calendar.timeInMillis

        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(name)
            )
        } else {
            // Нет права на точные будильники — ставим неточный
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(name)
            )
        }
    }

    override fun cancel() {
        alarmManager.cancel(pendingIntent(""))
    }

    // Один и тот же requestCode: новый будильник заменяет предыдущий
    private fun pendingIntent(name: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
            .putExtra(ReminderReceiver.EXTRA_NAME, name)
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        const val REQUEST_CODE = 0
    }
}
