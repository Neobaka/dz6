package com.example.dz6.presentation.edit

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dz6.di.ProfileDependencies
import com.example.dz6.domain.model.Profile
import com.example.dz6.domain.model.ReminderTime
import com.example.dz6.domain.usecase.ImportAvatarUseCase
import com.example.dz6.domain.usecase.ObserveProfileUseCase
import com.example.dz6.domain.usecase.SaveProfileUseCase
import com.example.dz6.domain.usecase.ScheduleReminderUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val observeProfile: ObserveProfileUseCase,
    private val saveProfile: SaveProfileUseCase,
    private val importAvatar: ImportAvatarUseCase,
    private val scheduleReminder: ScheduleReminderUseCase
) : ViewModel() {

    var fullName by mutableStateOf("")
        private set
    var position by mutableStateOf("")
        private set
    var resumeUrl by mutableStateOf("")
        private set
    var avatarUri by mutableStateOf<String?>(null)
        private set
    var reminderTime by mutableStateOf("")
        private set

    private var timeTouched by mutableStateOf(false)

    private val parsedTime: ReminderTime? get() = ReminderTime.parse(reminderTime)

    val nameError: Boolean get() = fullName.isBlank()
    /** Ошибку времени показываем после первого ввода, чтобы пустой экран не был красным. */
    val timeError: Boolean get() = timeTouched && parsedTime == null
    /** Сохранение недоступно, пока имя пустое или время не в формате HH:mm. */
    val canSave: Boolean get() = !nameError && parsedTime != null

    init {
        viewModelScope.launch {
            val p = observeProfile().first()
            fullName = p.fullName
            position = p.position
            resumeUrl = p.resumeUrl
            avatarUri = p.avatarUri
            reminderTime = p.reminderTime
        }
    }

    fun onNameChange(value: String) { fullName = value }
    fun onPositionChange(value: String) { position = value }
    fun onResumeUrlChange(value: String) { resumeUrl = value }

    fun onTimeChange(value: String) {
        reminderTime = value
        timeTouched = true
    }

    fun onAvatarPicked(uri: Uri) {
        viewModelScope.launch {
            runCatching { importAvatar(uri.toString()) }
                .onSuccess { avatarUri = it }
        }
    }

    fun onSave(onDone: () -> Unit) {
        val time = parsedTime
        if (!canSave || time == null) {
            timeTouched = true
            return
        }
        viewModelScope.launch {
            val name = fullName.trim()
            saveProfile(Profile(name, position.trim(), avatarUri, resumeUrl.trim(), time.toString()))
            scheduleReminder(time, name)
            onDone()
        }
    }

    companion object {
        fun factory(deps: ProfileDependencies) = viewModelFactory {
            initializer {
                EditProfileViewModel(
                    deps.observeProfile, deps.saveProfile, deps.importAvatar, deps.scheduleReminder
                )
            }
        }
    }
}
