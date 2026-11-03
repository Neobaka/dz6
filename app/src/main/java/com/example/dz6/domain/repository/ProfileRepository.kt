package com.example.dz6.domain.repository

import com.example.dz6.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<Profile>
    suspend fun saveProfile(profile: Profile)

    /** Копирует выбранную картинку во внутреннее хранилище и возвращает её URI. */
    suspend fun importAvatar(sourceUri: String): String
}
