package com.example.pocket.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.ui.screens.components.SearchBar
import com.example.pocket.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    isDarkModeSupported: Boolean = false,
    onDarkModeIconClicked: (() -> Unit) = {},
    viewModel: HomeScreenViewModel,
    onClickUrlItem: (UrlEntity) -> Unit,
    isDarkModeEnabled:Boolean = isSystemInDarkTheme()
) {
    val urlItems by viewModel.savedUrls.observeAsState()
    val filteredList by viewModel.filteredList.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var searchText by rememberSaveable { mutableStateOf("") }
    var searchBarExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!searchBarExpanded) {
            PocketAppBar(
                isDarkModeSupported = isDarkModeSupported,
                onDarkModeIconClicked = onDarkModeIconClicked,
                onSearchIconClicked = { searchBarExpanded = true },
                isDarkModeEnabled = isDarkModeEnabled
            )
        } else {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchText = searchText,
                onSearchTextChange = {
                    searchText = it
                    viewModel.onSearchTextValueChange(it)
                },
                onCloseIconClicked = { searchBarExpanded = false }
            )
        }

        UrlList(
            onFetchImageBitmap = { viewModel.getBitmap(it).asImageBitmap() },
            urlItems = (if (searchText.isBlank()) urlItems else filteredList) ?: listOf(),
            onClickItem = onClickUrlItem,
            onItemSwiped = {
                viewModel.deleteUrlItem(it)
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss() //if there is another snack bar,dismiss it
                    val snackBarResult = snackbarHostState.showSnackbar("Deleted", "Undo")
                    if (snackBarResult == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                }
            }
        )
    }
}


@Composable
fun PocketAppBar(
    isDarkModeSupported: Boolean = false,
    isDarkModeEnabled: Boolean = isSystemInDarkTheme(),
    onSearchIconClicked: (() -> Unit)? = null,
    onDarkModeIconClicked: (() -> Unit)? = null
) {
    TopAppBar(contentPadding = PaddingValues(top = 8.dp, start = 8.dp)) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Pocket",
            style = MaterialTheme.typography.h1,
        )
        Column(modifier = Modifier.padding(8.dp)) {
            Icon(
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onSearchIconClicked?.invoke() },
                imageVector = Icons.Filled.Search,
                contentDescription = ""
            )
        }
        if (!isDarkModeSupported) {
            Icon(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onDarkModeIconClicked?.invoke() },
                imageVector = if (isDarkModeEnabled) Icons.Filled.DarkMode else Icons.Outlined.DarkMode,
                contentDescription = "Dark mode icon",
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun UrlList(
    urlItems: List<UrlEntity>,
    onClickItem: (UrlEntity) -> Unit,
    onItemSwiped: (UrlEntity) -> Unit = {},
    onFetchImageBitmap: suspend (String) -> ImageBitmap,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            vertical = 8.dp,
            horizontal = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = urlItems,
            key = { it.id }
        ) { urlItem ->
            UrlCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onClickItem(urlItem) },
                onFetchImageBitmap = onFetchImageBitmap,
                onCardSwiped = onItemSwiped,
                urlItem = urlItem
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun UrlCard(
    modifier: Modifier = Modifier,
    onFetchImageBitmap: suspend (String) -> ImageBitmap,
    onCardSwiped: (UrlEntity) -> Unit = {},
    urlItem: UrlEntity,
    color: Color = MaterialTheme.colors.surface
) {
    Log.d(TAG, "UrlCard: ")
    var imageBitmapState by remember { mutableStateOf<ImageBitmap?>(null) }
    val dismissState = rememberDismissState {
        if (it == DismissValue.DismissedToEnd) {
            onCardSwiped(urlItem)
            true
        } else false
    }

    SwipeToDismiss(
        state = dismissState,
        background = {},
        directions = setOf(DismissDirection.StartToEnd)
    ) {
        Card(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color)
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = urlItem.contentTitle,
                        style = MaterialTheme.typography.h1
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = urlItem.host,
                        style = MaterialTheme.typography.caption
                    )
                }
                urlItem.imageAbsolutePath?.let {
                    LaunchedEffect(urlItem.id) { imageBitmapState = onFetchImageBitmap(it) }
                }
                imageBitmapState?.let {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = it,
                        contentDescription = "Thumbnail",
                    )
                }
            }
        }
    }
}

