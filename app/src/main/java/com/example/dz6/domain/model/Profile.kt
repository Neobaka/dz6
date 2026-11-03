package com.example.dz6.domain.model

data class Profile(
    val fullName: String = "",
    val position: String = "",
    /** URI аватарки во внутреннем хранилище приложения (file://...) */
    val avatarUri: String? = null,
    /** Ссылка на резюме/портфолио (pdf) */
    val resumeUrl: String = "",
    /** Время любимой пары в формате HH:mm */
    val reminderTime: String = ""
)
