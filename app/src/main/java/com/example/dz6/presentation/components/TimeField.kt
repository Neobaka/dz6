package com.example.dz6.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.example.dz6.R
import com.example.dz6.domain.model.ReminderTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Время любимой пары") },
        placeholder = { Text("HH:mm") },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        supportingText = { if (isError) Text("Некорректный формат времени") },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(painterResource(R.drawable.ic_access_time), contentDescription = "Выбрать время")
            }
        }
    )

    if (showPicker) {
        val initial = ReminderTime.parse(value) ?: Calendar.getInstance().let {
            ReminderTime(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
        }
        val state = rememberTimePickerState(
            initialHour = initial.hour,
            initialMinute = initial.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(ReminderTime(state.hour, state.minute).toString())
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Отмена") } },
            text = { TimePicker(state = state) }
        )
    }
}
