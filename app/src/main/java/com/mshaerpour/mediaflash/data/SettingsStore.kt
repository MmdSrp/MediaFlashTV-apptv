package com.mshaerpour.mediaflash.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "media_flash_store")

/**
 * Persistence for app-level settings and user data.
 * Stored via AndroidX DataStore (Preferences variant) - survives app restarts.
 *
 * Includes:
 * - Selected UI language ("fa" | "en")
 * - Favorites set of file URI strings
 * - Recent playback list (newest first, capped at 50)
 */
class SettingsStore(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val FAVORITES = stringSetPreferencesKey("favorites")
        val RECENT = stringSetPreferencesKey("recent")
        val LAST_USB_ROOT = stringPreferencesKey("last_usb_root")
    }

    val language: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "fa" }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = value }
    }

    val favorites: Flow<Set<String>> = context.dataStore.data.map { it[Keys.FAVORITES] ?: emptySet() }

    suspend fun toggleFavorite(fileKey: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITES] ?: emptySet()
            prefs[Keys.FAVORITES] = if (current.contains(fileKey)) current - fileKey else current + fileKey
        }
    }

    val recent: Flow<List<String>> = context.dataStore.data.map { prefs ->
        (prefs[Keys.RECENT] ?: emptySet()).toList()
    }

    suspend fun addRecent(fileKey: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.RECENT] ?: emptySet()).toMutableList()
            current.remove(fileKey)
            current.add(0, fileKey)
            // Cap at 50 entries to keep the list scannable
            if (current.size > 50) current.subList(50, current.size).clear()
            prefs[Keys.RECENT] = current.toSet()
        }
    }

    suspend fun clearRecent() {
        context.dataStore.edit { it.remove(Keys.RECENT) }
    }

    val lastUsbRoot: Flow<String?> = context.dataStore.data.map { it[Keys.LAST_USB_ROOT] }

    suspend fun setLastUsbRoot(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri == null) prefs.remove(Keys.LAST_USB_ROOT)
            else prefs[Keys.LAST_USB_ROOT] = uri
        }
    }

    /** Convenience accessor used by MainActivity. */
    private fun Preferences?.getStringOrNull(key: Preferences.Key<String>): String? {
        return this?.get(key)
    }
}
