package com.mshaerpour.mediaflash.data

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.mshaerpour.mediaflash.util.FileType
import com.mshaerpour.mediaflash.util.FileUtils

/**
 * One browsable file or folder. Wraps a DocumentFile so we can play, browse, or
 * inspect it across the whole UI.
 */
data class MediaFile(
    val uri: Uri,
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String?,
    val parentUri: Uri?,
    val fileType: FileType,
) {
    /** Convenience for the file extension display. */
    val extension: String
        get() = FileUtils.extension(name)

    /** Stable key for navigation, favorites and recent list. */
    val key: String
        get() = uri.toString()
}
