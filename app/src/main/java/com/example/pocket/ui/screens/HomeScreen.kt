package com.example.pocket.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.pocket.R
import com.example.pocket.data.domain.SavedUrlItem
import com.example.pocket.ui.activities.HandleUrlActivity.Companion.SAVE_URL_WORKERS_TAG
import com.example.pocket.ui.components.PocketAppBar
import com.example.pocket.ui.components.SavedUrlItemCard
import com.example.pocket.ui.components.SearchBar
import com.example.pocket.ui.components.rememberSearchBarState
import com.example.pocket.ui.navigation.PocketNavigationDestinations
import com.example.pocket.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavController,
    isDarkModeSupported: Boolean = false,
    onDarkModeOptionClicked: (() -> Unit) = {},
    onClickUrlItem: (SavedUrlItem) -> Unit,
    onSignOutButtonClick: () -> Unit,
    isDarkModeEnabled: Boolean = isSystemInDarkTheme(),
) {
    val snackbarMessage = stringResource(id = R.string.label_item_deleted)
    val snackbarActionLabel = stringResource(id = R.string.label_undo)
    val savedUrlItems by homeScreenViewModel.savedUrlItems.observeAsState()
    val filteredUrlItems by homeScreenViewModel.filteredUrlItems.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState(isCloseIconVisible = true)
    var searchText by rememberSaveable { mutableStateOf("") }
    var searchBarExpanded by rememberSaveable { mutableStateOf(false) }
    var isDropDownMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var isAlertDialogVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val workerManager = remember { WorkManager.getInstance(context) }
    val saveUrlWorkerState by workerManager
        .getWorkInfosByTagLiveData(SAVE_URL_WORKERS_TAG)
        .observeAsState()
    val dropDownMenuContent = @Composable {
        DropdownMenuItem(
            onClick = {
                isDropDownMenuExpanded = false
                isAlertDialogVisible = true
            },
            text = { Text(text = stringResource(id = R.string.label_log_out)) }
        )
        if (!isDarkModeSupported) {
            DropdownMenuItem(
                onClick = { onDarkModeOptionClicked() },
                text = { Text(text = stringResource(id = if (isDarkModeEnabled) R.string.label_turn_off_dark_mode else R.string.label_turn_on_dark_mode)) }
            )
        }
    }
    if (isAlertDialogVisible) {
        val confirmButton = @Composable {
            TextButton(
                onClick = {
                    onSignOutButtonClick()
                    isDropDownMenuExpanded = false
                    isAlertDialogVisible = false
                    navController.navigate(PocketNavigationDestinations.WELCOME_SCREEN) {
                        popUpTo(PocketNavigationDestinations.HOME_SCREEN) {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.label_yes),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        val dismissButton = @Composable {
            TextButton(onClick = { isAlertDialogVisible = false }) {
                Text(
                    text = stringResource(id = R.string.label_no),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        AlertDialog(
            title = {
                Text(
                    text = stringResource(id = R.string.label_log_out_message),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.label_log_out_alert_dialog_text),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            onDismissRequest = { isAlertDialogVisible = false },
            confirmButton = { confirmButton() },
            dismissButton = { dismissButton() }
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
                        homeScreenViewModel.onSearchTextValueChange(it)
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
            AnimatedVisibility(saveUrlWorkerState?.any { it.state == WorkInfo.State.ENQUEUED } == true) {
                Banner("Waiting for network to update")
            }
            AnimatedVisibility(saveUrlWorkerState?.any { it.state == WorkInfo.State.RUNNING } == true) {
                Banner("Refreshing")
            }
            UrlList(
                modifier = Modifier.fillMaxSize(),
                fetchImageBitmap = homeScreenViewModel::getImageBitmap,
                urlItems = (if (searchText.isBlank()) savedUrlItems else filteredUrlItems)
                    ?: listOf(),
                onClickItem = onClickUrlItem,
                onItemSwiped = { urlEntity ->
                    homeScreenViewModel.deleteUrlItem(urlEntity)
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss() //if there is another snack bar,dismiss it
                        val snackBarResult =
                            snackbarHostState.showSnackbar(snackbarMessage, snackbarActionLabel)
                        if (snackBarResult == SnackbarResult.ActionPerformed) homeScreenViewModel.undoDelete()
                    }
                }
            )
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
    urlItems: List<SavedUrlItem>,
    onClickItem: (SavedUrlItem) -> Unit,
    onItemSwiped: (SavedUrlItem) -> Unit = {},
    fetchImageBitmap: suspend (String) -> ImageBitmap,
) {
    if (urlItems.isEmpty()) ListEmptyMessage(modifier = Modifier.fillMaxSize())
    else LazyColumn(
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
                savedUrlItem = urlItem
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SwipeToDismissUrlCard(
    modifier: Modifier = Modifier,
    fetchImageBitmap: suspend (String) -> ImageBitmap,
    onCardSwiped: (SavedUrlItem) -> Unit = {},
    savedUrlItem: SavedUrlItem,
) {
    var thumbnailBitmapState by remember { mutableStateOf<ImageBitmap?>(null) }
    var faviconBitmapState by remember { mutableStateOf<ImageBitmap?>(null) }
    val dismissState = rememberDismissState {
        if (it == DismissValue.DismissedToEnd) {
            onCardSwiped(savedUrlItem)
            true
        } else false
    }
    savedUrlItem.imageAbsolutePath?.let {
        LaunchedEffect(savedUrlItem.id) { thumbnailBitmapState = fetchImageBitmap(it) }
    }

    savedUrlItem.faviconAbsolutePath?.let {
        LaunchedEffect(savedUrlItem.id) { faviconBitmapState = fetchImageBitmap(it) }
    }

    // TODO this is an m2 component
    SwipeToDismiss(
        state = dismissState,
        background = {},
        directions = setOf(DismissDirection.StartToEnd)
    ) {
        SavedUrlItemCard(
            modifier = modifier,
            savedUrlItem = savedUrlItem,
            thumbnail = thumbnailBitmapState,
            favicon = faviconBitmapState
        )
    }
}

@Composable
private fun Banner(text: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error
        ),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}