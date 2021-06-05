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
fun UrlCard(
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
        Card(modifier = modifier) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (thumbnailBitmapState == null) {
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
                        bitmap = thumbnailBitmapState!!,
                        contentDescription = "Thumbnail",
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.5f),
                    text = urlItem.contentTitle,
                    style = MaterialTheme.typography.h1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                UrlCardFooter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f),
                    hostName = urlItem.host,
                    thumbnailBitmapState = faviconBitmapState
                )
            }
        }
    }
}

@Composable
private fun UrlCardFooter(
    modifier: Modifier = Modifier,
    thumbnailBitmapState: ImageBitmap? = null,
    hostName: String
) {
    Row(modifier = modifier) {
        thumbnailBitmapState?.let {
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
