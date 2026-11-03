package com.example.dz6.domain.repository

import com.example.dz6.domain.model.ReminderTime

interface ReminderScheduler {
    /** Ставит напоминание на сегодня; если время уже прошло — на завтра. */
    fun schedule(time: ReminderTime, name: String)
    fun cancel()
}
