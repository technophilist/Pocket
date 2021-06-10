package com.example.pocket.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pocket.data.database.UrlEntity

@ExperimentalMaterialApi
@Composable
fun SwipeToDismissUrlCard(
    modifier: Modifier = Modifier,
    onFetchImageBitmap: suspend (String) -> ImageBitmap,
    onCardSwiped: (UrlEntity) -> Unit = {},
    urlItem: UrlEntity,
) {
    var thumbnailBitmapState by remember { mutableStateOf<ImageBitmap?>(null) }
    var faviconBitmapState by remember { mutableStateOf<ImageBitmap?>(null) }
    val dismissState = rememberDismissState {
        if (it == DismissValue.DismissedToEnd) {
            onCardSwiped(urlItem)
            true
        } else false
    }
    urlItem.imageAbsolutePath?.let {
        LaunchedEffect(urlItem.id) { thumbnailBitmapState = onFetchImageBitmap(it) }
    }

    urlItem.faviconAbsolutePath?.let {
        LaunchedEffect(urlItem.id) { faviconBitmapState = onFetchImageBitmap(it) }
    }

    SwipeToDismiss(
        state = dismissState,
        background = {},
        directions = setOf(DismissDirection.StartToEnd)
    ) {
        UrlCard(
            modifier = modifier,
            contentTitle = urlItem.contentTitle,
            hostName = urlItem.host,
            favicon = faviconBitmapState,
            thumbnail = thumbnailBitmapState
        )
    }
}

 /**
  * This composable represents a url card.If the thumbnail is null
  * then a placeholder with a grey background will be displayed.
  *
  * @param modifier The modifier to be applied to the card.
  * @param thumbnail The image to be displayed as the thumbnail.
  * @param favicon The favicon image of the webpage.
  * @param contentTitle The title of the webpage.
  * @param hostName The name of the host of the webpage.
  * */
@Composable
fun UrlCard(
    modifier: Modifier = Modifier,
    thumbnail: ImageBitmap? = null,
    favicon: ImageBitmap? = null,
    contentTitle: String,
    hostName: String
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (thumbnail == null) {
                //if the thumbnail is null,display a grey placeholder
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                        .background(Color.Gray),
                )

            } else {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f),
                    bitmap = thumbnail,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop
                )
            }

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
