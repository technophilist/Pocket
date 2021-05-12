package com.example.pocket.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onClickUrlItem: (UrlEntity) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = FocusRequester()

    val urlItems by viewModel.savedUrls.observeAsState()
    val filteredList by viewModel.filteredList.observeAsState()
    var isSearchIconVisible by remember { mutableStateOf(true) }
    var isCloseIconVisible by remember { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val trailingIcon = @Composable {
        if (isCloseIconVisible) {
            Icon(
                modifier = Modifier.clickable {
                    searchText = ""
                    isSearchIconVisible = true
                    isCloseIconVisible = false
                    focusManager.clearFocus()
                },
                imageVector = Icons.Filled.Close,
                contentDescription = "Close Icon"
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it == FocusState.Active) {
                            isSearchIconVisible = false
                            isCloseIconVisible = true
                        }
                    }
                    .offset()
                    .fillMaxWidth()
                    .padding(8.dp),
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.onSearchTextValueChange(it)
                },
                label = { Text(text = "Search...") },
                leadingIcon = { if (isSearchIconVisible) Icon(Icons.Filled.Search, "Search Icon") },
                trailingIcon = trailingIcon,
                singleLine = true,
                keyboardActions = KeyboardActions(onSearch = {
                    if (searchText.isBlank()) {
                        isSearchIconVisible = true
                        isCloseIconVisible = false
                    }
                    focusManager.clearFocus()
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            UrlList(
                onFetchImageBitmap = { viewModel.getBitmap(it).asImageBitmap() },
                urlItems = (if (searchText.isBlank()) urlItems else filteredList) ?: listOf(),
                onClickItem = onClickUrlItem,
                onItemSwiped = {
                    viewModel.deleteUrlItem(it)
                    coroutineScope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss() //if there is another snack bar,dismiss it
                        val snackBarResult = snackBarHostState.showSnackbar("Deleted", "Undo")
                        if (snackBarResult == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                    }
                }
            )
        }
        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState
        )
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


