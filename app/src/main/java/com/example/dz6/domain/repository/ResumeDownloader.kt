package com.example.dz6.domain.repository

interface ResumeDownloader {
    /** Скачивает файл по ссылке и по завершении открывает его в просмотрщике. */
    fun download(url: String)
}
