package com.googlium.googliumnotes

import java.io.Serializable

data class Note(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var content: String,
    var imageUri: String? = null
) : Serializable
