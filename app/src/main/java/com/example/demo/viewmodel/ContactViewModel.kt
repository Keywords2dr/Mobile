package com.example.demo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.data.AppDatabase
import com.example.demo.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).contactDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage

    val contacts: StateFlow<List<Contact>> = dao.getAllContacts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredContacts: StateFlow<List<Contact>> = combine(
        contacts,
        _searchQuery
    ) { contacts, query ->
        if (query.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addContact(name: String, phone: String): Boolean {
        if (name.isBlank() || phone.isBlank()) return false
        viewModelScope.launch {
            dao.insertContact(Contact(name = name, phone = phone))
        }
        _userMessage.value = "Đã thêm liên hệ"
        return true
    }

    fun updateContact(id: Int, name: String, phone: String): Boolean {
        if (name.isBlank() || phone.isBlank()) return false
        viewModelScope.launch {
            dao.updateContact(Contact(id = id, name = name, phone = phone))
        }
        _userMessage.value = "Đã lưu thay đổi"
        return true
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            dao.deleteContact(contact)
        }
        _userMessage.value = "Đã xóa liên hệ"
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}