package com.example.dz6.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Хранит аватарки во внутреннем хранилище: разрешения не нужны, URI не "протухает". */
class AvatarFileStorage(private val context: Context) {

    private val dir get() = File(context.filesDir, "avatars").apply { mkdirs() }

    suspend fun import(sourceUri: String): String = withContext(Dispatchers.IO) {
        // Новое имя каждый раз, чтобы Coil не показывал закешированную старую картинку
        val target = File(dir, "avatar_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(Uri.parse(sourceUri))?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Не удалось открыть $sourceUri")
        Uri.fromFile(target).toString()
    }

    /** Удаляет все аватарки, кроме актуальной. */
    suspend fun cleanupExcept(currentUri: String?) = withContext(Dispatchers.IO) {
        val keep = currentUri?.let { Uri.parse(it).path }
        dir.listFiles()?.filter { it.absolutePath != keep }?.forEach { it.delete() }
    }
}
