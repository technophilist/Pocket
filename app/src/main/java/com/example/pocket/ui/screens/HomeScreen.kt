package com.example.pocket.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.viewmodels.HomeScreenViewModel


@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    lifecycleCoroutineScope: LifecycleCoroutineScope,
    onClickUrlItem:(UrlEntity)->Unit
) {
    val urlItems by viewModel.savedUrls.observeAsState()
    val filteredList by viewModel.filteredList.observeAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester()
    var isSearchIconVisible by remember {
        mutableStateOf(true)
    }
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { if (it == FocusState.Active) isSearchIconVisible = false }
                .offset()
                .fillMaxWidth()
                .padding(8.dp),
            value = searchText,
            onValueChange = {
                searchText = it
                lifecycleCoroutineScope.launchWhenStarted {
                    viewModel.filter(it)
                }
            },
            label = { Text(text = "Search...") },
            leadingIcon = {
                if(isSearchIconVisible){
                    Icon(Icons.Filled.Search, "Search Icon")
                }
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        searchText = ""
                        isSearchIconVisible = true
                        focusManager.clearFocus()
                    },
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close Icon"
                )
            },
            singleLine = true,
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
        UrlList(
            urlItems = (if (searchText.isBlank()) urlItems else filteredList) ?: listOf(),
            onClickItem = onClickUrlItem
        )
    }
}

@Composable
private fun UrlList(urlItems: List<UrlEntity>, onClickItem: (UrlEntity) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(urlItems) { urlItem ->
            UrlCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 8.dp)
                    .clickable { onClickItem(urlItem) },
                urlItem = urlItem
            )
        }
    }
}
