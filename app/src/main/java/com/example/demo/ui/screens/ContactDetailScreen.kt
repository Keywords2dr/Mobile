package com.example.demo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo.model.Contact
import com.example.demo.ui.components.DeleteConfirmDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    onSave: (String, String) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember(contact.id) { mutableStateOf(contact.name) }
    var phone by remember(contact.id) { mutableStateOf(contact.phone) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết liên hệ") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Quay lại")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên liên hệ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Vui lòng nhập đầy đủ tên và số điện thoại")
                            }
                        } else {
                            onSave(name, phone)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Lưu")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Xóa")
                }
            }
        }
    }

    if (showDeleteConfirm) {
        DeleteConfirmDialog(
            contact = contact,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            },
            onCancel = { showDeleteConfirm = false }
        )
    }
}