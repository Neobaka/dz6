package com.example.dz6.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dz6.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore by preferencesDataStore(name = "profile")

/**
 * Профиль — небольшая плоская структура из 4 строк, поэтому
 * Preferences DataStore подходит лучше, чем БД или ручной файл.
 */
class ProfileLocalDataSource(private val context: Context) {

    private object Keys {
        val name = stringPreferencesKey("full_name")
        val position = stringPreferencesKey("position")
        val avatar = stringPreferencesKey("avatar_uri")
        val resume = stringPreferencesKey("resume_url")
    }

    val profile: Flow<Profile> = context.profileDataStore.data.map { p ->
        Profile(
            fullName = p[Keys.name].orEmpty(),
            position = p[Keys.position].orEmpty(),
            avatarUri = p[Keys.avatar],
            resumeUrl = p[Keys.resume].orEmpty()
        )
    }

    suspend fun save(profile: Profile) {
        context.profileDataStore.edit { p ->
            p[Keys.name] = profile.fullName
            p[Keys.position] = profile.position
            p[Keys.resume] = profile.resumeUrl
            if (profile.avatarUri != null) p[Keys.avatar] = profile.avatarUri else p.remove(Keys.avatar)
        }
    }
}
