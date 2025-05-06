package com.example.sawaapplication.navigation.bottomBar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CustomBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    navController: NavController
) {
    val items = listOf(
        Icons.Default.Home,
        Icons.Default.Dashboard,
        Icons.Default.Notifications,
        Icons.Default.Groups,
        Icons.Default.Person
    )

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 4.dp,
        modifier = Modifier.height(80.dp)
    ) {
        items.forEachIndexed { index, icon ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { onItemSelected(index) }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}

