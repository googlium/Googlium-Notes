package com.googlium.googliumnotes

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object NoteRepository {
    private val _notes = MutableLiveData<List<Note>>(emptyList())
    val notes: LiveData<List<Note>> = _notes
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        val file = File(context.filesDir, "notes.json")
        if (file.exists()) {
            val json = file.readText()
            val array = JSONArray(json)
            val list = mutableListOf<Note>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Note(
                        id = obj.getLong("id"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        imageUri = if (obj.isNull("imageUri")) null else obj.getString("imageUri"),
                    )
                )
            }
            _notes.value = list
        }
        initialized = true
    }

    private fun save(context: Context) {
        val array = JSONArray()
        _notes.value?.forEach { note ->
            val obj = JSONObject()
            obj.put("id", note.id)
            obj.put("title", note.title)
            obj.put("content", note.content)
            obj.put("imageUri", note.imageUri ?: JSONObject.NULL)
            array.put(obj)
        }
        val file = File(context.filesDir, "notes.json")
        file.writeText(array.toString())
    }

    fun addNote(context: Context, note: Note) {
        val currentList = _notes.value.orEmpty().toMutableList()
        currentList.add(0, note)
        _notes.value = currentList
        save(context)
    }

    fun updateNote(context: Context, updatedNote: Note) {
        val currentList = _notes.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedNote.id }
        if (index != -1) {
            currentList[index] = updatedNote
            _notes.value = currentList
            save(context)
        }
    }

    fun deleteNote(context: Context, noteId: Long) {
        val currentList = _notes.value.orEmpty().toMutableList()
        currentList.removeAll { it.id == noteId }
        _notes.value = currentList
        save(context)
    }
}
