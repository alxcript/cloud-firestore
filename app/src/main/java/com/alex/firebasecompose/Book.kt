package com.alex.firebasecompose

import com.google.firebase.firestore.DocumentId

data class Book(
    @DocumentId
    val id: String = "",
    val author: String = "",
    val pages: String = "",
    val title: String = "",
    val years: String = ""
)