package com.example.demo.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.demo.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContactsCursor(): Cursor

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactByIdCursor(id: Long): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContactSync(contact: Contact): Long

    @Query("UPDATE contacts SET name = :name, phone = :phone WHERE id = :id")
    fun updateContactById(id: Long, name: String, phone: String): Int

    @Query("DELETE FROM contacts WHERE id = :id")
    fun deleteContactById(id: Long): Int
}