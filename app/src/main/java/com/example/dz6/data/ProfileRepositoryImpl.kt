package com.example.dz6.data

import com.example.dz6.domain.model.Profile
import com.example.dz6.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val local: ProfileLocalDataSource,
    private val avatars: AvatarFileStorage
) : ProfileRepository {

    override fun observeProfile(): Flow<Profile> = local.profile

    override suspend fun saveProfile(profile: Profile) {
        local.save(profile)
        avatars.cleanupExcept(profile.avatarUri)
    }

    override suspend fun importAvatar(sourceUri: String): String = avatars.import(sourceUri)
}
