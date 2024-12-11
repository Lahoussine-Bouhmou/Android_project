package com.automacorp

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.automacorp.ui.theme.AutomacorpTheme

/**
 * A composable function that represents the top app bar for the Automacorp application.
 * It adapts its appearance based on whether a title is provided.
 *
 * @param title The title to display in the app bar. If null, no title is shown.
 * @param returnAction The action to perform when the return (back) button is clicked.
 * @param openRoomAction The action to perform when the room icon is clicked.
 * @param openGithubAction The action to perform when the GitHub icon is clicked.
 * @param sendEmailAction The action to perform when the email icon is clicked.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomacorpTopAppBar(
    title: String? = null,
    returnAction: () -> Unit = {},
    openRoomAction: () -> Unit = {},
    openGithubAction: () -> Unit = {},
    sendEmailAction: () -> Unit = {}
) {
    // Define the color scheme for the TopAppBar based on the light theme
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer, // Background color of the app bar
        titleContentColor = MaterialTheme.colorScheme.primary, // Color of the title text
    )

    // Define the actions to be displayed on the app bar
    val actions: @Composable RowScope.() -> Unit = {
        // IconButton for navigating to the Room section
        IconButton(onClick = { openRoomAction() }) {
            Icon(
                painter = painterResource(R.drawable.ic_rooms), // Room icon drawable
                contentDescription = stringResource(R.string.app_go_room_description) // Description for accessibility
            )
        }
        // IconButton for sending an email
        IconButton(onClick = { sendEmailAction() }) {
            Icon(
                painter = painterResource(R.drawable.ic_mail), // Mail icon drawable
                contentDescription = stringResource(R.string.app_go_mail_description) // Description for accessibility
            )
        }
        // IconButton for opening GitHub profile
        IconButton(onClick = { openGithubAction() }) {
            Icon(
                painter = painterResource(R.drawable.ic_github), // GitHub icon drawable
                contentDescription = stringResource(R.string.app_go_github_description) // Description for accessibility
            )
        }
    }

    // Determine which type of TopAppBar to display based on the presence of a title
    if (title == null) {
        // Display a simple TopAppBar without a title
        TopAppBar(
            title = { Text("") }, // Empty title
            colors = colors, // Apply the defined color scheme
            actions = actions // Add the defined actions
        )
    } else {
        // Display a MediumTopAppBar with a title and a navigation (back) icon
        MediumTopAppBar(
            title = { Text(title) }, // Display the provided title
            colors = colors, // Apply the defined color scheme
            // Navigation icon (e.g., back button) for screens other than the main screen
            navigationIcon = {
                IconButton(onClick = returnAction) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Back arrow icon
                        contentDescription = stringResource(R.string.app_go_back_description) // Description for accessibility
                    )
                }
            },
            actions = actions // Add the defined actions
        )
    }
}

/**
 * Preview of the AutomacorpTopAppBar without a title (e.g., Home Screen).
 */
@Preview(showBackground = true)
@Composable
fun AutomacorpTopAppBarHomePreview() {
    // Apply the AutomacorpTheme with light mode enabled
    AutomacorpTheme(darkTheme = false) { // Ensure light mode by setting darkTheme to false
        AutomacorpTopAppBar(null) // No title for the home screen app bar
    }
}

/**
 * Preview of the AutomacorpTopAppBar with a title (e.g., Details Screen).
 */
@Preview(showBackground = true)
@Composable
fun AutomacorpTopAppBarPreview() {
    // Apply the AutomacorpTheme with light mode enabled
    AutomacorpTheme(darkTheme = false) { // Ensure light mode by setting darkTheme to false
        AutomacorpTopAppBar("A page") // Provide a title for the details screen app bar
    }
}
