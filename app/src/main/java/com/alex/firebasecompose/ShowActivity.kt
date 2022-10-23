package com.alex.firebasecompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alex.firebasecompose.ui.theme.FirebaseComposeTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShowActivity : ComponentActivity() {

    private val TAG = "GETDATA"

    private var bookList = mutableStateListOf<Book>()

    private fun listMap() {
        val db = Firebase.firestore

        db.collection("libros")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val book = document.toObject(Book::class.java)
                    this.bookList.add(book)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(this.TAG, "Error getting documents: ", exception)
            }
    }

    private fun deleteBook(book: Book) {
        val db = Firebase.firestore
        db.collection("libros").document(book.id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        this.bookList.remove(book)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listMap()
        setContent {
            FirebaseComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        val searchedValue = remember { mutableStateOf("") }
                        SearchBox(searchedValue)

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(items = bookList.filter { book ->
                                book.title.contains(searchedValue.value, ignoreCase = true) ||
                                        book.author.contains(searchedValue.value, ignoreCase = true)
                            }, key = { book -> book.id }) { book ->
                                BookCard(book)
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun BookCard(book: Book) {
        Card(elevation = 1.dp) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row {
                    Text("ID: ${book.id}", fontWeight = FontWeight.W700)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.TopEnd)) {
                        var showMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.MoreVert, "")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(onClick = {
                                deleteBook(book)
                            }) {
                                Text(text = "Delete")
                            }
                        }
                    }
                }
                Text("Author: ${book.author}", color = Color.Gray)
                Text("Title: ${book.title}", color = Color.Gray)
                Text("Pages: ${book.pages}", color = Color.Gray)
                Text("Year: ${book.years}", color = Color.Gray)

            }
        }
    }

    @Composable
    fun SearchBox(searchedValue: MutableState<String>) {
        Row(horizontalArrangement = Arrangement.Center) {
            OutlinedTextField(
                value = searchedValue.value,
                onValueChange = { searchedValue.value = it },
                label = { Text("Search") }, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
