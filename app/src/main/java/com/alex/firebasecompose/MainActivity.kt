package com.alex.firebasecompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.alex.firebasecompose.ui.theme.FirebaseComposeTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    companion object {
        var TAG = "DATO"
    }

    private var hashMap = hashMapOf<String, String>()

    fun cargarValor(author: String, pages: String, title: String, year: String) {
        hashMap["author"] = author
        hashMap["pages"] = pages
        hashMap["title"] = title
        hashMap["years"] = year
    }

    fun guardarMapa() {
        val db = Firebase.firestore

        db.collection("libros")
            .add(hashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Datos ingresados con id: ${documentReference.id}")
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "Se produjo un error: ", error)
            }

        hashMap.clear()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var author by remember { mutableStateOf("") }
                        var pages by remember { mutableStateOf("") }
                        var title by remember { mutableStateOf("") }
                        var year by remember { mutableStateOf("") }

                        val onAuthorTextChange = { text: String -> author = text }
                        val onPagesTextChange = { text: String -> pages = text }
                        val onTitleTextChange = { text: String -> title = text }
                        val onYearTextChange = { text: String -> year = text }

                        val focusRequester = remember {FocusRequester()}

                        ValueTextfieldFocusable("Author", author, onAuthorTextChange, focusRequester)
                        ValueTextfield("Pages", pages, onPagesTextChange)
                        ValueTextfield("Title", title, onTitleTextChange)
                        ValueTextfield("Year", year, onYearTextChange)

                        Button(onClick = {
                            author = ""
                            pages = ""
                            title = ""
                            year = ""
                            focusRequester.requestFocus()
                            hashMap.clear()
                        }) {
                            Text("Clean")
                        }
                        Button(onClick = {
                            cargarValor(author, pages, title, year)
                            author = ""
                            pages = ""
                            title = ""
                            year = ""
                            focusRequester.requestFocus()
                            Toast.makeText(baseContext, "Load in memory.. Now, you can save to FireStore'", Toast.LENGTH_LONG).show()
                        }) {
                            Text("Load In HashMap")
                        }
                        Button(onClick = { guardarMapa() }, enabled = hashMap.isNotEmpty()) {
                            Text("Save To FireStore")
                        }
                        Button(onClick = {
                            val intent = Intent(baseContext, ShowActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text("List All")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ValueTextfield(title: String, value: String, onValueTextChange: (String) -> Unit) {
    OutlinedTextField(value = value,
        onValueChange = onValueTextChange,
        label = { Text(title) }
    )
}

@Composable
fun ValueTextfieldFocusable(title: String, value: String, onValueTextChange: (String) -> Unit, focusRequester: FocusRequester) {
    OutlinedTextField(value = value,
        onValueChange = onValueTextChange,
        label = { Text(title) },
        modifier = Modifier.focusRequester(focusRequester)
    )
}

