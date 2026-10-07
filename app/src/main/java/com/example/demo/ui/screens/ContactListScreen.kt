package com.example.demo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo.model.Contact
import com.example.demo.ui.components.ContactListItem
import com.example.demo.ui.components.DeleteConfirmDialog
import com.example.demo.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

@Composable
fun ContactListScreen(
    viewModel: ContactViewModel,
    onContactClick: (Contact) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var contactPendingDelete by remember { mutableStateOf<Contact?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp)
        ) {
            Text(
                text = "Danh Bạ Điện Thoại",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Tìm kiếm theo tên hoặc số điện thoại") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.filteredContacts, key = { it.id }) { contact ->
                    ContactListItem(
                        contact = contact,
                        onClick = { onContactClick(contact) },
                        onDeleteClick = { contactPendingDelete = contact }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên liên hệ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val success = viewModel.addContact(name, phone)
                    scope.launch {
                        if (success) {
                            snackbarHostState.showSnackbar("Đã thêm liên hệ")
                            name = ""
                            phone = ""
                        } else {
                            snackbarHostState.showSnackbar("Vui lòng nhập đầy đủ tên và số điện thoại")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Thêm liên hệ")
            }
        }
    }

    val toDelete = contactPendingDelete
    if (toDelete != null) {
        DeleteConfirmDialog(
            contact = toDelete,
            onConfirm = {
                viewModel.deleteContact(toDelete)
                contactPendingDelete = null
                scope.launch {
                    snackbarHostState.showSnackbar("Đã xóa liên hệ")
                }
            },
            onCancel = { contactPendingDelete = null }
        )
    }
}