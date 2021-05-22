package com.example.pocket.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction


class SearchBarState(
    val focusManager: FocusManager,
    val focusRequester: FocusRequester = FocusRequester(),
    isSearchIconVisible: Boolean = true,
    isCloseIconVisible: Boolean = true,
) {
    var isSearchIconVisible by mutableStateOf(isSearchIconVisible)
    var isCloseIconVisible by mutableStateOf(isCloseIconVisible)
}

@Composable
fun rememberSearchBarState(
    focusManager: FocusManager = LocalFocusManager.current,
    isSearchIconVisible: Boolean = true,
    isCloseIconVisible: Boolean = true
) = remember {
    SearchBarState(
        focusManager = focusManager,
        isSearchIconVisible = isSearchIconVisible,
        isCloseIconVisible = isCloseIconVisible
    )
}


@Composable
fun PocketSearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    state: SearchBarState = rememberSearchBarState(),
    keyboardActions: KeyboardActions = KeyboardActions(onSearch = {
        if (searchText.isBlank()) {
            state.isSearchIconVisible = true
            state.isCloseIconVisible = false
        }
        state.focusManager.clearFocus()
    }),
    onFocusChanged: ((FocusState) -> Unit) = {
        if (it == FocusState.Active) {
            state.isSearchIconVisible = false
            state.isCloseIconVisible = true
        }
    },
    onCloseIconClicked: (() -> Unit)? = null,
) {

    val trailingIcon = @Composable {
        if (state.isCloseIconVisible) {
            Icon(
                modifier = Modifier.clickable {
                    onCloseIconClicked?.invoke()
                    state.apply {
                        isSearchIconVisible = true
                        isCloseIconVisible = false
                        focusManager.clearFocus()
                    }
                },
                imageVector = Icons.Filled.Close,
                contentDescription = "Close Icon"
            )
        }
    }

    TextField(
        modifier = modifier
            .focusRequester(state.focusRequester)
            .onFocusChanged { onFocusChanged(it) },
        value = searchText,
        onValueChange = { onSearchTextChange(it) },
        label = { Text(text = "Search...") },
        leadingIcon = { if (state.isSearchIconVisible) Icon(Icons.Filled.Search, "Search Icon") },
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )

}

