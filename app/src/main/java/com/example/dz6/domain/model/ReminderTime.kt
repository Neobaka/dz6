package com.example.dz6.domain.model

/** Время напоминания в формате HH:mm (без java.time, чтобы работало на minSdk 24). */
data class ReminderTime(val hour: Int, val minute: Int) {

    override fun toString(): String =
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

    companion object {
        private val regex = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")

        /** Возвращает null, если строка не соответствует формату HH:mm. */
        fun parse(text: String): ReminderTime? {
            val match = regex.matchEntire(text.trim()) ?: return null
            return ReminderTime(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        }
    }
}
