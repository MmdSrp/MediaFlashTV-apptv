package com.mshaerpour.mediaflash.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mshaerpour.mediaflash.data.UsbDrive
import com.mshaerpour.mediaflash.data.UsbStorageManager
import com.mshaerpour.mediaflash.data.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel shared between top-level screens for things like the active USB drive,
 * the list of detected drives, and selected language.
 */
class SharedViewModel(app: Application) : AndroidViewModel(app) {

    private val usbManager = UsbStorageManager(app)
    val settings = SettingsStore(app)

    private val _drives = MutableStateFlow<List<UsbDrive>>(emptyList())
    val drives: StateFlow<List<UsbDrive>> = _drives.asStateFlow()

    private val _selectedDrive = MutableStateFlow<UsbDrive?>(null)
    val selectedDrive: StateFlow<UsbDrive?> = _selectedDrive.asStateFlow()

    /** Refresh the list of detected USB drives in the background. */
    fun refreshDrives() {
        viewModelScope.launch {
            _drives.value = usbManager.listUsbDrives()
        }
    }

    fun selectDrive(drive: UsbDrive) {
        _selectedDrive.value = drive
        viewModelScope.launch { settings.setLastUsbRoot(drive.rootUri.toString()) }
    }

    fun getDriveById(id: String): UsbDrive? = _drives.value.firstOrNull { it.id == id }

    companion object {
        fun provideFactory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = SharedViewModel(app) as T
            }
    }
}
