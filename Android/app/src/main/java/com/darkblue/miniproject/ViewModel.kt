package com.darkblue.miniproject

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class ViewModel(
    private val repository: FileRepository = FileRepository()
): ViewModel() {

    fun uploadImage(file: File) {
        viewModelScope.launch {
            repository.uploadImage(file)
        }
    }

    private val _state = MutableStateFlow(UIState())
    fun onEvent(event: UIEvent) {
        when(event){
            UIEvent.openCamera -> {
                _state.update { it.copy(
                    isCameraOpen = true
                )}
            }
            UIEvent.closeCamera -> {
                _state.update { it.copy(
                    isCameraOpen = false
                )}
            }
            UIEvent.openImage -> {
                _state.update { it.copy(
                    isImageShown = false
                )}
            }
            UIEvent.closeImage -> {
                _state.update { it.copy(
                    isImageShown = false
                )}
            }
        }
    }
}