package com.example.dz6.domain.usecase

import com.example.dz6.domain.model.ReminderTime
import com.example.dz6.domain.repository.ReminderScheduler

class ScheduleReminderUseCase(private val scheduler: ReminderScheduler) {
    operator fun invoke(time: ReminderTime, name: String) = scheduler.schedule(time, name)
}
