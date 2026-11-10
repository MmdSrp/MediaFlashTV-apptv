package com.mshaerpour.mediaflash.data

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Detects removable USB volumes mounted on the Android TV device.
 *
 * On API >= 24 we use StorageManager to enumerate StorageVolumes marked as removable.
 * On older devices we fall back to Environment.getExternalStorageDirectory() and any
 * well-known secondary mount paths.
 *
 * The user must grant SAF access (see UsbAccessActivity / SAF) before we can read file
 * trees - we expose them via DocumentFile.fromTreeUri(rootUri).
 */
class UsbStorageManager(private val context: Context) {

    /**
     * Returns a list of detected removable storage volumes (USB flash drives).
     * On Android 11+ (API 30+) the user must separately grant SAF access at runtime
     * to actually read these volumes - this method only enumerates them.
     */
    suspend fun listUsbDrives(): List<UsbDrive> = withContext(Dispatchers.IO) {
        val result = mutableListOf<UsbDrive>()
        try {
            val storageManager =
                context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
            val volumes = storageManager?.storageVolumes ?: emptyList()
            for (volume in volumes) {
                if (volume.isRemovable || volume.isEmulated) {
                    val name = volume.getDescription(context) ?: "USB Drive"
                    val rootUri = volumeRootUri(volume) ?: continue
                    val (total, free) = queryVolumeSize(rootUri)
                    result.add(
                        UsbDrive(
                            id = volume.uuid ?: rootUri.toString(),
                            name = name,
                            rootUri = rootUri,
                            totalBytes = total,
                            freeBytes = free,
                            isRemovable = volume.isRemovable,
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Missing permissions - return what we have
        }
        // If nothing detected (older Android), attempt a known removable path
        if (result.isEmpty()) {
            val legacyRoot = legacyRemovableRoot()
            if (legacyRoot != null) {
                val (total, free) = queryVolumeSize(legacyRoot)
                result.add(
                    UsbDrive(
                        id = "legacy-usb",
                        name = "USB Drive",
                        rootUri = legacyRoot,
                        totalBytes = total,
                        freeBytes = free,
                        isRemovable = true,
                    )
                )
            }
        }
        result
    }

    /** Returns the SAF tree Uri for a StorageVolume, or null if not accessible. */
    private fun volumeRootUri(volume: StorageVolume): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Safe path: try to access the directory URI directly
                val intent = volume.createOpenDocumentTreeIntent()
                val extras = intent.extras
                val rootUri = extras?.get("android.provider.extra.INITIAL_URI") as? Uri
                rootUri ?: volume.directoryUriSafe()
            } else {
                volume.directoryUriSafe()
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Best-effort fallback for older API levels. */
    private fun StorageVolume.directoryUriSafe(): Uri? = try {
        // Reflection-based fallback for compatibility
        val method = this.javaClass.getMethod("getPathFile")
        val file = method.invoke(this) as? java.io.File
        file?.let { Uri.fromFile(it) }
    } catch (e: Exception) {
        null
    }

    /** Legacy removable path detection (pre-KitKat). */
    private fun legacyRemovableRoot(): Uri? {
        val candidates = listOf(
            "/mnt/usb_storage",
            "/mnt/usb",
            "/storage/USBdrive",
            "/storage/usbotg",
            "/mnt/media_rw",
        )
        for (path in candidates) {
            val f = java.io.File(path)
            if (f.exists() && f.isDirectory && f.canRead()) {
                return Uri.fromFile(f)
            }
        }
        return null
    }

    /** Returns (total, free) bytes via DocumentFile from the root tree URI. */
    private fun queryVolumeSize(rootUri: Uri): Pair<Long, Long> {
        return try {
            // Try Android's StatFs for file:// URIs
            if (rootUri.scheme == "file") {
                val path = rootUri.path
                if (path != null) {
                    val stat = android.os.StatFs(path)
                    val total = stat.totalBytes
                    val free = stat.availableBytes
                    return Pair(total, free)
                }
            }
            // For SAF tree URIs we can't easily query size; return a placeholder
            Pair(0L, 0L)
        } catch (e: Exception) {
            Pair(0L, 0L)
        }
    }
}
