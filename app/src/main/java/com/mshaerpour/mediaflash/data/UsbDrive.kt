package com.mshaerpour.mediaflash.data

import android.net.Uri

/**
 * Represents a mounted USB drive (or other external storage volume) available on the TV.
 */
data class UsbDrive(
    val id: String,
    val name: String,
    val rootUri: Uri,
    val totalBytes: Long,
    val freeBytes: Long,
    val isRemovable: Boolean = true,
)
