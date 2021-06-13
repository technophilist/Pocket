package com.example.pocket.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


/**
 * This composable represents a url card.If the thumbnail is null
 * then a placeholder with a grey background will be displayed.
 *
 * @param modifier The modifier to be applied to the card.
 * @param favicon The favicon image of the webpage.
 * @param contentTitle The title of the webpage.
 * @param hostName The name of the host of the webpage.
 * */
@Composable
fun UrlCard(
    modifier: Modifier = Modifier,
    favicon: ImageBitmap? = null,
    contentTitle: String,
    hostName: String,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                content = content
            )

            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f),
                text = contentTitle,
                style = MaterialTheme.typography.h1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            UrlCardFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                hostName = hostName,
                favicon = favicon
            )
        }
    }
}


@Composable
private fun UrlCardFooter(
    modifier: Modifier = Modifier,
    favicon: ImageBitmap? = null,
    hostName: String
) {
    Row(modifier = modifier) {
        favicon?.let {
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .alignByBaseline(),
                bitmap = it,
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .alignByBaseline(),
            text = hostName,
            style = MaterialTheme.typography.caption
        )
    }
}

