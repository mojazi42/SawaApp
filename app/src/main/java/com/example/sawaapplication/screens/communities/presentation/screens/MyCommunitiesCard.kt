package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.R
import com.example.sawaapplication.ui.theme.black
import com.example.sawaapplication.ui.theme.white

@Composable
fun MyCommunitiesCard(
    /*
        community: Community,
    */
) {
    /*    val communityImage = community.image
        val communityName = community.name
        val communityDescription = community.description ?: ""
        val communityMembers = community.members*/

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = integerResource(id = R.integer.extraSmallSpace).dp),
        colors = CardDefaults.cardColors(
            containerColor = white,
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.inversePrimary
        ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  needed to  implementation "io.coil-kt:coil-compose:2.5.0"
            /*         AsyncImage(
                         model = community.imageUrl,
                         contentDescription = community.name,
                         contentScale = ContentScale.Crop,
                         modifier = Modifier
                             .size(80.dp)
                             .clip(CircleShape)
                     )*/
            /*            Image(
                            painter ="" ,
                            *//*painterResource(id = communityImage)*//*,
                contentDescription = ""*//*community.name*//*,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
            )*/

            Spacer(modifier = Modifier.width(integerResource(id = R.integer.smallerSpace).dp))

            Text(
                text = "Saudi innovation", /*communityName.name*/
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = black,
            )

            Spacer(modifier = Modifier.width(integerResource(id = R.integer.smallerSpace).dp))
                        //communityDescription?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = " Describe your community's purpose and vision. Highlight unique features and what members can \n" +
                                        "look forward to. Share common interests or goals\n" +
                                        " that bring everyone together.",
                                style = MaterialTheme.typography.bodySmall,
                                color = black,
                                modifier = Modifier.padding(integerResource(id = R.integer.extraSmallSpace).dp)
                            )
                       // }

        }
    }
}


