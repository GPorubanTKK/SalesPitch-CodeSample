package com.rld.datingapp.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable fun LabeledTextField(
    label: @Composable () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeHolder: String = ""
) {
    Column {
        label()
        TextField(value, onValueChange, modifier, placeholder = { Text(placeHolder) })
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
        TextField("$value", {
            if(it.matches("\\d+".toRegex()) || it.isEmpty()) onValueChange(it.toInt())
        }, modifier, placeholder = { Text(placeHolder) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
    }
}