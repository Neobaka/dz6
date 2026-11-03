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
import com.example.dz6.domain.usecase.ImportAvatarUseCase
import com.example.dz6.domain.usecase.ObserveProfileUseCase
import com.example.dz6.domain.usecase.SaveProfileUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val observeProfile: ObserveProfileUseCase,
    private val saveProfile: SaveProfileUseCase,
    private val importAvatar: ImportAvatarUseCase
) : ViewModel() {

    var fullName by mutableStateOf("")
        private set
    var position by mutableStateOf("")
        private set
    var resumeUrl by mutableStateOf("")
        private set
    var avatarUri by mutableStateOf<String?>(null)
        private set

    val canSave: Boolean get() = fullName.isNotBlank()

    init {
        viewModelScope.launch {
            val p = observeProfile().first()
            fullName = p.fullName
            position = p.position
            resumeUrl = p.resumeUrl
            avatarUri = p.avatarUri
        }
    }

    fun onNameChange(value: String) { fullName = value }
    fun onPositionChange(value: String) { position = value }
    fun onResumeUrlChange(value: String) { resumeUrl = value }

    fun onAvatarPicked(uri: Uri) {
        viewModelScope.launch {
            runCatching { importAvatar(uri.toString()) }
                .onSuccess { avatarUri = it }
        }
    }

    fun onSave(onDone: () -> Unit) {
        if (!canSave) return
        viewModelScope.launch {
            saveProfile(Profile(fullName.trim(), position.trim(), avatarUri, resumeUrl.trim()))
            onDone()
        }
    }

    companion object {
        fun factory(deps: ProfileDependencies) = viewModelFactory {
            initializer { EditProfileViewModel(deps.observeProfile, deps.saveProfile, deps.importAvatar) }
        }
    }
}
