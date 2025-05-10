package com.example.sawaapplication.navigation.bottomBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.R
import com.example.sawaapplication.ui.theme.firstOrange

@Composable
fun CustomBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.height(integerResource(id = R.integer.bottomBarHeight).dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                selected = selectedIndex == 0,
                icon =R.drawable.home,
                contentDescription = "Home",
                onClick = { onItemSelected(0) }
            )

            BottomBarItem(
                selected = selectedIndex == 1,
                iconVector = Icons.Rounded.GridView ,
                contentDescription = "Explore",
                onClick = { onItemSelected(1) }
            )

            BottomBarItem(
                selected = selectedIndex == 2,
                iconVector = Icons.Filled.Groups ,
                contentDescription = "Community",
                onClick = { onItemSelected(2) }
            )

            BottomBarItem(
                selected = selectedIndex == 3,
                iconVector = Icons.Filled.Email ,
                contentDescription = "Chat",
                onClick = { onItemSelected(3) }
            )
        }
    }
}

@Composable
fun BottomBarItem(
    selected: Boolean,
    icon: Int? = null,
    iconVector: ImageVector? = null,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                tint = if (selected) firstOrange else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(integerResource(id = R.integer.bottomBarIconSize).dp)
            )
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = contentDescription,
                tint = if (selected) firstOrange else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(integerResource(id = R.integer.bottomBarIconSize).dp)
            )
        }
    }
}
