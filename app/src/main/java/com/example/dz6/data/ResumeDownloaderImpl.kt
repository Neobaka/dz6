package com.example.dz6.data

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.dz6.domain.repository.ResumeDownloader

class ResumeDownloaderImpl(private val context: Context) : ResumeDownloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun download(url: String) {
        val fileName = URLUtil.guessFileName(url, null, "application/pdf")
        val request = try {
            DownloadManager.Request(url.toUri())
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, "Некорректная ссылка", Toast.LENGTH_SHORT).show()
            return
        }
            .setTitle(fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // Публичная папка Downloads: на API 29+ разрешения не нужны, до API 28 — WRITE_EXTERNAL_STORAGE
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val id = downloadManager.enqueue(request)
        Toast.makeText(context, "Скачивание началось", Toast.LENGTH_SHORT).show()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) != id) return
                context.unregisterReceiver(this)
                openDownloaded(id)
            }
        }
        // ACTION_DOWNLOAD_COMPLETE — системный broadcast, на Android 14+ флаг обязателен
        ContextCompat.registerReceiver(
            context, receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun openDownloaded(id: Long) {
        val uri = downloadManager.getUriForDownloadedFile(id)
        if (uri == null) {
            Toast.makeText(context, "Не удалось скачать файл", Toast.LENGTH_SHORT).show()
            return
        }
        val mime = downloadManager.getMimeTypeForDownloadedFile(id) ?: "application/pdf"
        val view = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mime)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(view)
        } catch (e: ActivityNotFoundException) {
            // Нет просмотрщика pdf — показываем список загрузок
            context.startActivity(
                Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
