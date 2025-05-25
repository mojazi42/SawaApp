package com.example.sawaapplication.navigation.bottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.R

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
)

@Composable
fun CustomBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val navigationItems = listOf(
        NavigationItem(stringResource(R.string.Home), Icons.Filled.Home),
        NavigationItem(stringResource(R.string.Explore), Icons.Rounded.GridView),
        NavigationItem(stringResource(R.string.Communities), Icons.Default.Group),
        NavigationItem(stringResource(R.string.chats), Icons.Filled.ChatBubble),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    if (selectedIndex == index) {
                        Box(
                            modifier = Modifier
                                .size(integerResource(id = R.integer.IconSize).dp)
                                .shadow(
                                    elevation = integerResource(id = R.integer.IconShadowElevation).dp,
                                    shape = CircleShape,
                                    clip = false
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = Color.White
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = Color.Gray
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selectedIndex == index)
                            MaterialTheme.colorScheme.primary
                        else Color.Gray
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}