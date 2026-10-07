package com.example.demo.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.demo.model.Contact

@Composable
fun DeleteConfirmDialog(
    contact: Contact,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Xác nhận xóa") },
        text = { Text("Bạn có chắc muốn xóa liên hệ \"${contact.name}\" không?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Xóa")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Hủy")
            }
        }
    )
}