package com.example.demo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.demo.model.Contact

class ContactViewModel : ViewModel() {
    private var nextId = 1

    var contacts = mutableStateListOf<Contact>()
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedContact by mutableStateOf<Contact?>(null)
        private set

    val filteredContacts: List<Contact>
        get() = if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.phone.contains(searchQuery, ignoreCase = true)
            }
        }

    init {
        addContact("Nguyen Van A", "0123456789")
        addContact("Le Thi B", "0987654321")
        addContact("Tran Van C", "0121987654")
    }

    fun addContact(name: String, phone: String): Boolean {
        if (name.isBlank() || phone.isBlank()) return false
        contacts.add(Contact(nextId, name, phone))
        nextId++
        return true
    }

    fun updateContact(id: Int, name: String, phone: String): Boolean {
        if (name.isBlank() || phone.isBlank()) return false
        val index = contacts.indexOfFirst { it.id == id }
        if (index == -1) return false
        val updated = Contact(id, name, phone)
        contacts[index] = updated
        selectedContact = updated
        return true
    }

    fun deleteContact(contact: Contact) {
        contacts.remove(contact)
        if (selectedContact?.id == contact.id) {
            selectedContact = null
        }
    }

    fun selectContact(contact: Contact) {
        selectedContact = contact
    }


    fun clearSelection() {
        selectedContact = null
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
}