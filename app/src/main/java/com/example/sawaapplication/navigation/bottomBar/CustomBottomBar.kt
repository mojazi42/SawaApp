package com.example.sawaapplication.navigation.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CustomBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    onItemSelected(index)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selectedIndex == index)
                           MaterialTheme.colorScheme.primary
                        else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =  MaterialTheme.colorScheme.background,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
)

val navigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Filled.Home,
    ),
    NavigationItem(
        title = "Explore",
        icon = Icons.Rounded.GridView,
    ),
    NavigationItem (
        title = "Communities",
        icon = Icons.Default.Group,
    ),
    NavigationItem(
        title = "Chats",
        icon = Icons.Filled.ChatBubble,
    )
)
