package com.example.pocket.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocket.R
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.di.AppContainer
import com.example.pocket.ui.components.PocketAppBar
import com.example.pocket.ui.components.SearchBar
import com.example.pocket.ui.components.UrlCard
import com.example.pocket.ui.components.rememberSearchBarState
import com.example.pocket.ui.navigation.PocketNavigationDestinations
import com.example.pocket.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    appContainer: AppContainer,
    navController: NavController,
    isDarkModeSupported: Boolean = false,
    onDarkModeOptionClicked: (() -> Unit) = {},
    onClickUrlItem: (UrlEntity) -> Unit,
    isDarkModeEnabled: Boolean = isSystemInDarkTheme(),
    viewModel: HomeScreenViewModel,
) {
    val urlItems by viewModel.savedUrls.observeAsState()
    val filteredList by viewModel.filteredList.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState(isCloseIconVisible = true)
    var searchText by rememberSaveable { mutableStateOf("") }
    var searchBarExpanded by rememberSaveable { mutableStateOf(false) }
    var isDropDownMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var isAlertDialogVisible by rememberSaveable { mutableStateOf(false) }
    val dropDownMenuContent = @Composable {
        DropdownMenuItem(
            onClick = {
                isDropDownMenuExpanded = false
                isAlertDialogVisible = true
            },
            content = { Text(text = stringResource(id = R.string.label_log_out)) }
        )
        if (!isDarkModeSupported) {
            DropdownMenuItem(onClick = { onDarkModeOptionClicked() }) {
                Text(text = stringResource(id = if (isDarkModeEnabled) R.string.label_turn_off_dark_mode else R.string.label_turn_on_dark_mode))
            }
        }
    }
    if (isAlertDialogVisible) {
        AlertDialog(
            title = {
                Text(
                    text = stringResource(id = R.string.label_log_out_message),
                    style = MaterialTheme.typography.h6,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.label_log_out_alert_dialog_text),
                    style = MaterialTheme.typography.body1,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            onDismissRequest = { isAlertDialogVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) { appContainer.authenticationService.signOut() }
                        isDropDownMenuExpanded = false
                        isAlertDialogVisible = false
                        navController.navigate(PocketNavigationDestinations.WELCOME_SCREEN)
                    },
                    content = {
                        Text(
                            text = stringResource(id = R.string.label_yes),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.button
                        )
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { isAlertDialogVisible = false },
                    content = {
                        Text(
                            text = stringResource(id = R.string.label_no),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.button
                        )
                    }
                )
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!searchBarExpanded) {
                PocketAppBar(
                    onSearchIconClicked = { searchBarExpanded = true },
                    isDropDownMenuExpanded = isDropDownMenuExpanded,
                    onDropDownMenuDismissRequest = { isDropDownMenuExpanded = false },
                    onDropDownMenuIconClicked = { isDropDownMenuExpanded = true },
                    dropDownMenuContent = dropDownMenuContent
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
                    val snackbarMessage = stringResource(id = R.string.label_item_deleted)
                    val snackbarActionLabel = stringResource(id = R.string.label_undo)
                    UrlList(
                        modifier = Modifier.fillMaxSize(),
                        fetchImageBitmap = { urlString ->
                            viewModel.getBitmap(urlString).asImageBitmap()
                        },
                        urlItems = (if (searchText.isBlank()) urlItems else filteredList)
                            ?: listOf(),
                        onClickItem = onClickUrlItem,
                        onItemSwiped = { urlEntity ->
                            viewModel.deleteUrlItem(urlEntity)
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss() //if there is another snack bar,dismiss it
                                val snackBarResult =
                                    snackbarHostState.showSnackbar(
                                        snackbarMessage,
                                        snackbarActionLabel
                                    )
                                if (snackBarResult == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                            }
                        }
                    )
                }
            }
        }
        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackbarHostState
        )
    }
}

@Composable
private fun ListEmptyMessage(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 0.dp),
            text = stringResource(id = R.string.label_list_empty_header),
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.label_list_empty_desc),
            textAlign = TextAlign.Center
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun UrlList(
    modifier: Modifier = Modifier,
    urlItems: List<UrlEntity>,
    onClickItem: (UrlEntity) -> Unit,
    onItemSwiped: (UrlEntity) -> Unit = {},
    fetchImageBitmap: suspend (String) -> ImageBitmap,
) {
    LazyColumn(
        modifier = modifier,
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
