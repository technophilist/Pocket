package com.example.pocket.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pocket.data.domain.SavedUrlItem

// has a fixed height of 300dp
@Composable
fun SavedUrlItemCard(
    modifier: Modifier = Modifier,
    savedUrlItem: SavedUrlItem,
    thumbnail: ImageBitmap? = null,
    favicon: ImageBitmap? = null
) {
    Card(
        modifier = Modifier
        .height(300.dp)
        .then(modifier)
    ) {
        if (savedUrlItem.imageAbsolutePath == null) {
            //if the image path is null,display the host with primary background
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = savedUrlItem.url.host,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        } else {
            thumbnail?.let { imageBitmap ->
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    bitmap = imageBitmap,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            modifier = Modifier.padding(8.dp),
            text = savedUrlItem.title,
            style = MaterialTheme.typography.displayLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        UrlCardFooter(
            modifier = Modifier.fillMaxWidth(),
            hostName = savedUrlItem.url.host,
            favicon = favicon
        )
    }
}


@Composable
private fun UrlCardFooter(
    modifier: Modifier = Modifier,
    favicon: ImageBitmap? = null,
    hostName: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        favicon?.let {
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
                    .clip(CircleShape),
                bitmap = it,
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop
            )
        }
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = hostName,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
