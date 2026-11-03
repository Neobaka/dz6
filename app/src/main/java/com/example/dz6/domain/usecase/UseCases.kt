package com.example.dz6.domain.usecase

import com.example.dz6.domain.model.Profile
import com.example.dz6.domain.repository.ProfileRepository
import com.example.dz6.domain.repository.ResumeDownloader
import kotlinx.coroutines.flow.Flow

class ObserveProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<Profile> = repository.observeProfile()
}

class SaveProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile) = repository.saveProfile(profile)
}

class ImportAvatarUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(sourceUri: String): String = repository.importAvatar(sourceUri)
}

class DownloadResumeUseCase(private val downloader: ResumeDownloader) {
    operator fun invoke(url: String) = downloader.download(url)
}
