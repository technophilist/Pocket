package com.example.pocket.ui.screens

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onClickUrlItem: (UrlEntity) -> Unit
) {
    val urlItems by viewModel.savedUrls.observeAsState()
    val filteredList by viewModel.filteredList.observeAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester()
    var isSearchIconVisible by remember { mutableStateOf(true) }
    var isCloseIconVisible by remember { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

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
            trailingIcon = {
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
            },
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
            imageBitmap = {
                withContext(Dispatchers.IO) { viewModel.getBitmap(it).asImageBitmap() }
            },
            urlItems = (if (searchText.isBlank()) urlItems else filteredList) ?: listOf(),
            onClickItem = onClickUrlItem
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun UrlList(
    imageBitmap: suspend (String) -> ImageBitmap,
    urlItems: List<UrlEntity>,
    onClickItem: (UrlEntity) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(urlItems) { urlItem ->
            UrlCard(
                imageBitmap = imageBitmap,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
                    .height(200.dp)
                    .padding(start = 8.dp,end=8.dp,bottom = 8.dp)
                    .clickable { onClickItem(urlItem) },
                urlItem = urlItem,
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun UrlCard(
    modifier: Modifier = Modifier,
    imageBitmap: suspend (String) -> ImageBitmap,
    urlItem: UrlEntity,
    color: Color = MaterialTheme.colors.surface
) {
    var imageBitmapState by remember { mutableStateOf<ImageBitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberDismissState()

    SwipeToDismiss(state = dismissState, background = { /*TODO*/ }) {
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
                    coroutineScope.launch { imageBitmapState = imageBitmap(it) }
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


