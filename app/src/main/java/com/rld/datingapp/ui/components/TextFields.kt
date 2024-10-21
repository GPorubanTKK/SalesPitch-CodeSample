package com.rld.datingapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable fun LabeledTextField(
    label: @Composable () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeHolder: String = "",
    singleLine: Boolean = true
) {
    Column {
        label()
        TextField(
            value,
            onValueChange,
            modifier,
            placeholder = { Text(placeHolder) },
            singleLine = singleLine
        )
    }
}

@Composable fun NumberTextField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    placeHolder: String = "",
    label: @Composable () -> Unit = {}
) {
    Column {
        label()
        TextField(
            "$value",
            { if(it.matches("\\d+".toRegex()) || it.isEmpty()) onValueChange(it.toInt()) },
            modifier,
            placeholder = { Text(placeHolder) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Composable fun PasswordTextField(
    label: @Composable () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeHolder: String = ""
) {
    Column {
        label()
        TextField(
            value,
            onValueChange,
            modifier,
            placeholder = { Text(placeHolder) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
    }
}