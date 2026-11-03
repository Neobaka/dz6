package com.example.dz6.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dz6.di.ProfileDependencies
import com.example.dz6.domain.model.Profile
import com.example.dz6.domain.usecase.DownloadResumeUseCase
import com.example.dz6.domain.usecase.ObserveProfileUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    observeProfile: ObserveProfileUseCase,
    private val downloadResume: DownloadResumeUseCase
) : ViewModel() {

    val profile: StateFlow<Profile> = observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Profile())

    fun onResumeClick() {
        val url = profile.value.resumeUrl
        if (url.isNotBlank()) downloadResume(url)
    }

    companion object {
        fun factory(deps: ProfileDependencies) = viewModelFactory {
            initializer { ProfileViewModel(deps.observeProfile, deps.downloadResume) }
        }
    }
}
