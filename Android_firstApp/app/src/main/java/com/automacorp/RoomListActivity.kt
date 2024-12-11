// RoomListActivity.kt
package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.model.RoomViewModel
import com.automacorp.ui.theme.AutomacorpTheme
import com.automacorp.ui.theme.PurpleGrey80
import kotlinx.coroutines.flow.asStateFlow

class RoomListActivity : ComponentActivity() {
    // Initialize the RoomViewModel using the viewModels delegate
    private val roomViewModel: RoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge display for a modern UI experience

        // Define the action to perform when a room item is clicked
        val onRoomItemClick: (roomId: Long) -> Unit = { roomId ->
            // Display a toast message indicating which room was clicked
            Toast.makeText(
                baseContext,
                "You clicked on room $roomId",
                Toast.LENGTH_LONG
            ).show()
            // Navigate back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Define the action to navigate back to MainActivity
        val onNavigateBack: () -> Unit = {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setContent {
            // Apply the custom AutomacorpTheme with light mode enabled
            AutomacorpTheme(darkTheme = false) { // Ensure light mode by setting darkTheme to false
                // Fetch all rooms when the activity is created
                LaunchedEffect(Unit) {
                    roomViewModel.findAll()
                }

                // Collect the current state of rooms from the ViewModel
                val roomsState by roomViewModel.roomsState.asStateFlow().collectAsState()

                // Scaffold provides the basic structure for the screen, including top bar and content
                Scaffold(
                    topBar = { AutomacorpTopAppBar(title = "Rooms", returnAction = onNavigateBack) }
                ) { innerPadding ->
                    // Handle different UI states based on the roomsState
                    when {
                        roomsState.error != null -> {
                            // Display an error message and an empty room list if there's an error
                            RoomList(
                                rooms = emptyList(),
                                onNavigateBack = onNavigateBack,
                                onRoomClick = onRoomItemClick,
                                viewModel = roomViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                            // Show a toast message indicating the error
                            Toast.makeText(
                                applicationContext,
                                "Error loading rooms: ${roomsState.error}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            // Display the list of rooms
                            RoomList(
                                rooms = roomsState.rooms,
                                onNavigateBack = onNavigateBack,
                                onRoomClick = onRoomItemClick,
                                viewModel = roomViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomItem(room: RoomDto, modifier: Modifier = Modifier) {
    // Card to display individual room details
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, PurpleGrey80),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Add horizontal padding for better spacing
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Space elements evenly
        ) {
            Column {
                // Display the room name in bold
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                // Display the target temperature
                Text(
                    text = "Target temperature: ${room.targetTemperature ?: "?"}°",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // Display the current temperature
            Text(
                text = "${room.currentTemperature ?: "?"}°",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Right,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun RoomList(
    rooms: List<RoomDto>,
    onNavigateBack: () -> Unit,
    onRoomClick: (id: Long) -> Unit,
    viewModel: RoomViewModel,
    modifier: Modifier = Modifier
) {
    // Apply the custom AutomacorpTheme with light mode enabled
    AutomacorpTheme(darkTheme = false) { // Ensure light mode by setting darkTheme to false
        // Scaffold provides the basic structure for the screen, including top bar and content
        Scaffold(
            topBar = { AutomacorpTopAppBar(title = "Rooms", returnAction = onNavigateBack) }
        ) { innerPadding ->
            if (rooms.isEmpty()) {
                // Display a message when no rooms are found
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No rooms found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                // Display the list of rooms using LazyColumn for efficient rendering
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(rooms, key = { room -> room.id }) { room ->
                        // Each room item is clickable and triggers the onRoomClick action
                        RoomItem(
                            room = room,
                            modifier = Modifier
                                .clickable { onRoomClick(room.id) }
                        )
                    }
                }
            }
        }
    }
}
