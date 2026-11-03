package com.example.dz6.di

import android.content.Context
import com.example.dz6.data.AvatarFileStorage
import com.example.dz6.data.ProfileLocalDataSource
import com.example.dz6.data.ProfileRepositoryImpl
import com.example.dz6.data.ReminderSchedulerImpl
import com.example.dz6.data.ResumeDownloaderImpl
import com.example.dz6.domain.usecase.*

/** Простой ручной DI. Если в проекте есть Hilt/Koin — замени на модуль. */
class ProfileDependencies private constructor(context: Context) {
    private val app = context.applicationContext
    private val repository = ProfileRepositoryImpl(ProfileLocalDataSource(app), AvatarFileStorage(app))
    private val downloader = ResumeDownloaderImpl(app)
    private val scheduler = ReminderSchedulerImpl(app)

    val observeProfile = ObserveProfileUseCase(repository)
    val saveProfile = SaveProfileUseCase(repository)
    val importAvatar = ImportAvatarUseCase(repository)
    val downloadResume = DownloadResumeUseCase(downloader)
    val scheduleReminder = ScheduleReminderUseCase(scheduler)

    companion object {
        @Volatile private var instance: ProfileDependencies? = null
        fun get(context: Context): ProfileDependencies =
            instance ?: synchronized(this) {
                instance ?: ProfileDependencies(context).also { instance = it }
            }
    }
}
