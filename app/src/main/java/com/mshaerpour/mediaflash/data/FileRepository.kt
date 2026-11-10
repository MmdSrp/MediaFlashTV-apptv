package com.mshaerpour.mediaflash.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.mshaerpour.mediaflash.util.FileType
import com.mshaerpour.mediaflash.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Reads the file tree of a selected USB drive using DocumentFile (SAF) APIs.
 *
 * The drive is represented by a SAF tree Uri granted via ACTION_OPEN_DOCUMENT_TREE.
 * We never touch the underlying file paths - this works on all Android versions
 * from API 21+ and respects scoped storage on newer Android.
 */
class FileRepository(private val context: Context) {

    /** Returns the root DocumentFile for the given SAF tree URI, or null if revoked. */
    fun rootFromTreeUri(rootUri: Uri): DocumentFile? {
        return DocumentFile.fromTreeUri(context, rootUri)
    }

    /** Lists immediate children of the given directory URI, sorted folders-first. */
    suspend fun listChildren(dirUri: Uri, sortBy: SortBy = SortBy.NameAsc): List<MediaFile> =
        withContext(Dispatchers.IO) {
            val dir = if (dirUri == Uri.EMPTY) null else DocumentFile.fromTreeUri(context, dirUri)
            val children = dir?.listFiles()?.toList() ?: emptyList()
            children.mapNotNull { docToMediaFile(it, parentUri = dirUri) }
                .sortedWith(sortBy.comparator)
        }

    /** Maps a DocumentFile into our app's MediaFile domain object. */
    private fun docToMediaFile(document: DocumentFile, parentUri: Uri?): MediaFile? {
        if (!document.exists() && !document.isDirectory) return null
        val name = document.name ?: return null
        val type = FileUtils.classify(name, document.isDirectory)
        return MediaFile(
            uri = document.uri,
            name = name,
            isDirectory = document.isDirectory,
            size = if (document.isDirectory) 0L else document.length(),
            lastModified = document.lastModified(),
            mimeType = document.type,
            parentUri = parentUri,
            fileType = type,
        )
    }

    /** Recursively searches the drive for files whose name contains [query]. */
    suspend fun search(rootUri: Uri, query: String, limit: Int = 200): List<MediaFile> =
        withContext(Dispatchers.IO) {
            if (query.isBlank()) return@withContext emptyList()
            val root = DocumentFile.fromTreeUri(context, rootUri) ?: return@withContext emptyList()
            val needle = query.trim().lowercase()
            val results = mutableListOf<MediaFile>()
            fun walk(dir: DocumentFile, depth: Int) {
                if (results.size >= limit || depth > 8) return
                for (child in dir.listFiles()) {
                    if (results.size >= limit) return
                    val name = child.name ?: continue
                    if (name.lowercase().contains(needle)) {
                        val type = FileUtils.classify(name, child.isDirectory)
                        results.add(
                            MediaFile(
                                uri = child.uri,
                                name = name,
                                isDirectory = child.isDirectory,
                                size = if (child.isDirectory) 0L else child.length(),
                                lastModified = child.lastModified(),
                                mimeType = child.type,
                                parentUri = dir.uri,
                                fileType = type,
                            )
                        )
                    }
                    if (child.isDirectory) walk(child, depth + 1)
                }
            }
            walk(root, 0)
            results
        }
}

/** Sort options exposed in the file browser toolbar. */
enum class SortBy(val comparator: Comparator<MediaFile>) {
    NameAsc(compareBy { it.name.lowercase() }),
    NameDesc(compareByDescending { it.name.lowercase() }),
    SizeAsc(compareBy { it.size }),
    SizeDesc(compareByDescending { it.size }),
    DateAsc(compareBy { it.lastModified }),
    DateDesc(compareByDescending { it.lastModified }),
    TypeAsc(compareBy { it.fileType.ordinal }.thenBy { it.name.lowercase() }),
}
