package com.example.demo.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.demo.model.Contact

class ContactProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.demo.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/contacts")

        private const val CONTACTS = 1
        private const val CONTACT_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "contacts", CONTACTS)
            addURI(AUTHORITY, "contacts/#", CONTACT_ID)
        }
    }

    private val dao: ContactDao
        get() = AppDatabase.getInstance(context!!).contactDao()

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = when (uriMatcher.match(uri)) {
            CONTACTS -> dao.getAllContactsCursor()
            CONTACT_ID -> dao.getContactByIdCursor(ContentUris.parseId(uri))
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (uriMatcher.match(uri) != CONTACTS) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
        val name = values?.getAsString("name")
            ?: throw IllegalArgumentException("Missing name")
        val phone = values.getAsString("phone")
            ?: throw IllegalArgumentException("Missing phone")

        val id = dao.insertContactSync(Contact(name = name, phone = phone))
        context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (uriMatcher.match(uri) != CONTACT_ID) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
        val name = values?.getAsString("name")
            ?: throw IllegalArgumentException("Missing name")
        val phone = values.getAsString("phone")
            ?: throw IllegalArgumentException("Missing phone")

        val count = dao.updateContactById(ContentUris.parseId(uri), name, phone)
        if (count > 0) context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (uriMatcher.match(uri) != CONTACT_ID) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
        val count = dao.deleteContactById(ContentUris.parseId(uri))
        if (count > 0) context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return count
    }

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        CONTACTS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.contacts"
        CONTACT_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.contacts"
        else -> null
    }
}