package com.tatlib.app

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object AppContextHolder {

    lateinit var context: Context
        private set

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun createImageUri(): Uri {

        val file = File.createTempFile(
            "tatlib_ocr_",
            ".jpg",
            context.cacheDir
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}