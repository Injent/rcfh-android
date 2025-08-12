package ru.rcfh.common

import java.io.File

data class AppFiles(
    val dataDir: File,
    val cacheDir: File,
    val filesDir: File,
)