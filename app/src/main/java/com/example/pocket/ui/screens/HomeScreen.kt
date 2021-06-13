package com.example.pocket.ui.screens

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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.ui.screens.components.SearchBar
import com.example.pocket.ui.screens.components.UrlCard
import com.example.pocket.ui.screens.components.rememberSearchBarState
import com.example.pocket.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    isDarkModeSupported: Boolean = false,
    onDarkModeIconClicked: (() -> Unit) = {},
    viewModel: HomeScreenViewModel,
    onClickUrlItem: (UrlEntity) -> Unit,
    isDarkModeEnabled: Boolean = isSystemInDarkTheme()
) {
    val urlItems by viewModel.savedUrls.observeAsState()
    val filteredList by viewModel.filteredList.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState(isCloseIconVisible = true)

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
                onCloseIconClicked = {
                    /*
                    We are emptying the search text so that
                    the UrlList() composable will list all the
                    urls from viewModel.savedUrls instead of
                    viewModel.filteredList.
                    */
                    searchText = ""
                    searchBarExpanded = false
                },
                state = searchBarState
            )
            /*
            Make the searchbar immediately focused when the user clicks on
            the search icon.
             */
            SideEffect {
                /*
                We are using a side effect to ensure that we are requesting
                focus only after the searchbar has composed.
                */
                searchBarState.focusRequester.requestFocus()
            }
        }

        urlItems?.let {
            if (it.isEmpty()) {
                ListEmptyMessage(modifier = Modifier.fillMaxSize())
            } else {
                UrlList(
                    fetchImageBitmap = { urlString ->
                        viewModel.getBitmap(urlString).asImageBitmap()
                    },
                    urlItems = (if (searchText.isBlank()) urlItems else filteredList) ?: listOf(),
                    onClickItem = onClickUrlItem,
                    onItemSwiped = { urlEntity ->
                        viewModel.deleteUrlItem(urlEntity)
                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss() //if there is another snack bar,dismiss it
                            val snackBarResult = snackbarHostState.showSnackbar("Deleted", "Undo")
                            if (snackBarResult == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                        }
                    }
                )
            }
        }
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

@Composable
private fun ListEmptyMessage(modifier: Modifier = Modifier) {
    val message = """
        It's easy to add items to Pocket.
        Use the share button of any browser 
        and tap on the Pocket icon to add
        an item.
         """.trimIndent()

    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 0.dp),
            text = "Your list is empty",
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = message,
            textAlign = TextAlign.Center
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun UrlList(
    urlItems: List<UrlEntity>,
    onClickItem: (UrlEntity) -> Unit,
    onItemSwiped: (UrlEntity) -> Unit = {},
    fetchImageBitmap: suspend (String) -> ImageBitmap,
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
            SwipeToDismissUrlCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .clickable { onClickItem(urlItem) },
                fetchImageBitmap = fetchImageBitmap,
                onCardSwiped = onItemSwiped,
                urlItem = urlItem
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SwipeToDismissUrlCard(
    modifier: Modifier = Modifier,
    fetchImageBitmap: suspend (String) -> ImageBitmap,
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
        LaunchedEffect(urlItem.id) { thumbnailBitmapState = fetchImageBitmap(it) }
    }

    urlItem.faviconAbsolutePath?.let {
        LaunchedEffect(urlItem.id) { faviconBitmapState = fetchImageBitmap(it) }
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
        ) {
            if (urlItem.imageAbsolutePath == null) {
                //if the image path is null,display the host with primary background
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .fillMaxWidth()
                        .weight(3f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = urlItem.host,
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            } else {
                thumbnailBitmapState?.let {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f),
                        bitmap = it,
                        contentDescription = "Thumbnail",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}


