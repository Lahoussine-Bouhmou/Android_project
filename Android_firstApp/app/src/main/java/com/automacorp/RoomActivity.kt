package com.automacorp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.model.RoomViewModel
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge display for a modern UI experience

        // Define actions for various buttons and intents
        val onRoomClick: () -> Unit = {
            // Navigate to RoomListActivity when room-related action is triggered
            val intent = Intent(this, RoomListActivity::class.java)
            startActivity(intent)
        }

        val sendEmail: () -> Unit = {
            // Create an intent to send an email to the specified address
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto://lahessine.bouhmou@gmail.com"))
            startActivity(intent)
        }

        val openGithub: () -> Unit = {
            // Open the specified GitHub profile in a web browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Lahoussine-Bouhmou"))
            startActivity(intent)
        }

        // Retrieve the ROOM_PARAM from the intent that started this activity
        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)

        // Initialize the RoomViewModel using the viewModels delegate
        val model: RoomViewModel by viewModels()
        model.findByNameOrId(param ?: "") // Fetch the room details based on the parameter

        // Define the action to save or update the room details
        val onRoomSave: () -> Unit = {
            if (model.room != null) {
                val roomDto: RoomDto = model.room as RoomDto
                model.updateRoom(roomDto.id, roomDto) // Update the room in the ViewModel
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG).show()
                // Navigate back to MainActivity after saving
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        // Define the action to navigate back to MainActivity
        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        // Set the content view using Jetpack Compose
        setContent {
            // Apply the custom AutomacorpTheme with light mode enabled
            AutomacorpTheme(darkTheme = false) { // Ensure light mode by setting darkTheme to false
                // Scaffold provides the basic structure for the screen, including top bar and FAB
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack, onRoomClick, openGithub, sendEmail) },
                    floatingActionButton = { RoomUpdateButton(onRoomSave) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Display the RoomDetail if the room exists, otherwise show NoRoom
                    if (model.room != null) {
                        RoomDetail(model, Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomDetail(model: RoomViewModel, modifier: Modifier = Modifier) {
    // Local state to hold the current room details for editing
    var localRoom by remember { mutableStateOf(model.room) }

    Column(modifier = modifier.padding(16.dp)) {
        // OutlinedTextField for editing the room's name
        OutlinedTextField(
            value = model.room?.name ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { newName ->
                // Update the localRoom state and propagate the change to the ViewModel
                localRoom = localRoom?.copy(name = newName)
                localRoom?.let { model.updateRoom(localRoom!!.id, it) }
            },
            placeholder = { Text(stringResource(R.string.act_room_name)) }, // Placeholder text from string resources
        )

        // Display the current temperature of the room
        Text(
            text = "Current Temperature: ${model.room?.currentTemperature}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Slider to adjust the target temperature
        Slider(
            value = model.room?.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = { newTemp ->
                // Update the localRoom state and propagate the change to the ViewModel
                localRoom = localRoom?.copy(targetTemperature = newTemp.toDouble())
                localRoom?.let { model.updateRoom(localRoom!!.id, it) }
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0, // No intermediate steps
            valueRange = 10f..28f // Define the range of the slider
        )

        // Display the selected target temperature, rounded to one decimal place
        Text(text = (round((model.room?.targetTemperature ?: 18.0) * 10) / 10).toString())
    }
}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    // Extended Floating Action Button for saving room details
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save), // Content description from string resources
            )
        },
        text = { Text(text = stringResource(R.string.act_room_save)) } // Button text from string resources
    )
}

@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    // Display a message indicating that no room is selected or available
    Text(
        text = stringResource(R.string.act_room_none), // Text from string resources
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier.padding(bottom = 4.dp)
    )
}
