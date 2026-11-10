package com.mshaerpour.mediaflash.util

/**
 * Type of a browsable file.
 */
enum class FileType {
    Folder,
    Video,
    Audio,
    Image,
    Subtitle,
    Document,
    Archive,
    Other,
    ;

    val isMedia: Boolean
        get() = this == Video || this == Audio || this == Image
}

/** File-type classification by extension. */
object FileUtils {

    private val VIDEO_EXTENSIONS = setOf(
        "mp4", "mkv", "avi", "mov", "flv", "wmv", "webm", "m4v", "3gp", "mpeg", "mpg", "ts", "m2ts", "vob"
    )

    private val AUDIO_EXTENSIONS = setOf(
        "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma", "opus", "amr", "mid", "midi", "aiff"
    )

    private val IMAGE_EXTENSIONS = setOf(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "heif", "svg", "tiff", "tif"
    )

    private val SUBTITLE_EXTENSIONS = setOf(
        "srt", "vtt", "ass", "ssa", "sub", "smi", "idx"
    )

    private val DOCUMENT_EXTENSIONS = setOf(
        "pdf", "txt", "doc", "docx", "rtf", "odt", "xls", "xlsx", "ppt", "pptx", "csv", "epub"
    )

    private val ARCHIVE_EXTENSIONS = setOf(
        "zip", "rar", "7z", "tar", "gz", "bz2", "xz"
    )

    /** Returns the lowercase file extension without the leading dot, or "" if none. */
    fun extension(name: String): String {
        val dot = name.lastIndexOf('.')
        return if (dot < 0 || dot == name.length - 1) "" else name.substring(dot + 1).lowercase()
    }

    /** Classifies a file by its name and folder flag. */
    fun classify(name: String, isDirectory: Boolean): FileType {
        if (isDirectory) return FileType.Folder
        val ext = extension(name)
        return when (ext) {
            in VIDEO_EXTENSIONS -> FileType.Video
            in AUDIO_EXTENSIONS -> FileType.Audio
            in IMAGE_EXTENSIONS -> FileType.Image
            in SUBTITLE_EXTENSIONS -> FileType.Subtitle
            in DOCUMENT_EXTENSIONS -> FileType.Document
            in ARCHIVE_EXTENSIONS -> FileType.Archive
            else -> FileType.Other
        }
    }

    /** Returns a human-readable file size, e.g. "1.4 GB". */
    fun humanReadableSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var value = bytes.toDouble()
        var unitIdx = 0
        while (value >= 1024.0 && unitIdx < units.size - 1) {
            value /= 1024.0
            unitIdx++
        }
        return String.format("%.1f %s", value, units[unitIdx])
    }

    /** Returns a human-readable duration like "1h 23m" or "4m 56s". */
    fun humanReadableDuration(seconds: Long): String {
        if (seconds <= 0) return "0:00"
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
        else String.format("%d:%02d", m, s)
    }

    /** Returns a short human-readable date for display. */
    fun humanReadableDate(epochMillis: Long): String {
        if (epochMillis <= 0) return ""
        val date = java.util.Date(epochMillis)
        val fmt = java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault())
        return fmt.format(date)
    }
}
