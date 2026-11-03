package com.example.dz6.presentation.edit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.dz6.presentation.components.Avatar
import com.example.dz6.presentation.components.TimeField
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    var showSourceDialog by rememberSaveable { mutableStateOf(false) }
    var pendingCameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Галерея: системный Photo Picker, разрешения на хранилище не нужны
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(viewModel::onAvatarPicked) }

    // Камера: системное приложение камеры пишет в наш FileProvider-URI
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) pendingCameraUri?.let(viewModel::onAvatarPicked)
    }

    // Android 13+: без POST_NOTIFICATIONS пуш не покажется. Профиль сохраняем в любом случае.
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.onSave(onDone) }

    val onSaveClick = {
        val needPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        if (needPermission) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.onSave(onDone)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onSaveClick, enabled = viewModel.canSave) {
                        Icon(Icons.Default.Check, contentDescription = "Готово")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Avatar(
                uri = viewModel.avatarUri,
                size = 128.dp,
                modifier = Modifier.clickable { showSourceDialog = true }
            )
            TextField(
                value = viewModel.fullName,
                onValueChange = viewModel::onNameChange,
                label = { Text("Имя") },
                singleLine = true,
                isError = viewModel.nameError,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = viewModel.position,
                onValueChange = viewModel::onPositionChange,
                label = { Text("Должность") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = viewModel.resumeUrl,
                onValueChange = viewModel::onResumeUrlChange,
                label = { Text("Ссылка") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TimeField(
                value = viewModel.reminderTime,
                onValueChange = viewModel::onTimeChange,
                isError = viewModel.timeError
            )
        }
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Выберите источник") },
            text = {
                Column {
                    TextButton(onClick = {
                        showSourceDialog = false
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) { Text("Галерея") }
                    TextButton(onClick = {
                        showSourceDialog = false
                        val uri = createCameraUri(context)
                        pendingCameraUri = uri
                        cameraLauncher.launch(uri)
                    }) { Text("Камера") }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showSourceDialog = false }) { Text("Отмена") } }
        )
    }
}

private fun createCameraUri(context: Context): Uri {
    val dir = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
