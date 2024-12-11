package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.automacorp.ui.theme.AutomacorpTheme

class MainActivity : ComponentActivity() {
    companion object {
        // Constant key used for passing data between activities
        const val ROOM_PARAM = "com.automacorp.room.attribute"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define the action to perform when the "Say Hello" button is clicked
        val onSayHelloButtonClick: (name: String) -> Unit = { name ->
            if (name.trim().isNotEmpty()) {
                // Create an intent to navigate to RoomActivity with the provided name
                val intent = Intent(this, RoomActivity::class.java).apply {
                    putExtra(ROOM_PARAM, name)
                }
                startActivity(intent)
            } else {
                // Show a toast message if the input is empty
                Toast.makeText(baseContext, "Please write something", Toast.LENGTH_LONG).show()
            }
        }

        // Set the content view using Jetpack Compose
        setContent {
            // Apply the custom AutomacorpTheme with light mode enabled
            AutomacorpTheme(darkTheme = false) { // Ensure light mode by setting darkTheme to false
                // Scaffold provides basic structure for the screen
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Greeting composable displays the main UI elements
                    Greeting(
                        onClick = onSayHelloButtonClick,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(onClick: (name: String) -> Unit, modifier: Modifier = Modifier) {
    // State to hold the user's input name
    var name by remember { mutableStateOf("") }

    // Arrange UI elements vertically
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // General padding for the column
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the application logo
        AppLogo(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        )

        // Welcome text
        Text(
            text = stringResource(R.string.act_main_welcome),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Credit text
        Text(
            text = "Made By Lahoussine Bouhmou",
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color.Green,
            textAlign = TextAlign.Center
        )

        // Input field for the user's name
        TextField(
            name = name,
            onValueChange = { name = it },
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        )

        // Button to trigger the action with the entered name
        Button(
            onClick = { onClick(name) },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.act_main_open))
        }
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    // Display the app's logo image
    Image(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = stringResource(R.string.app_logo_description),
        modifier = modifier
            .paddingFromBaseline(top = 100.dp)
            .height(80.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextField(name: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    // Outlined text field with an icon and placeholder
    OutlinedTextField(
        value = name,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = stringResource(R.string.act_main_fill_name),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(R.string.act_main_fill_name))
            }
        },
        singleLine = true, // Ensure the text field is single-lined
        colors = TextFieldDefaults.outlinedTextFieldColors(
            // Define colors for the text field in light mode
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    )
}
