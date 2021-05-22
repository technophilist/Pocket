package com.example.pocket.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation


class SearchBarState(
    val focusManager: FocusManager,
    val focusRequester: FocusRequester = FocusRequester(),
    isSearchIconVisible: Boolean = true,
    isCloseIconVisible: Boolean = false,
) {
    var isSearchIconVisible by mutableStateOf(isSearchIconVisible)
    var isCloseIconVisible by mutableStateOf(isCloseIconVisible)
}

@Composable
fun rememberSearchBarState(
    focusManager: FocusManager = LocalFocusManager.current,
    isSearchIconVisible: Boolean = true,
    isCloseIconVisible: Boolean = false
) = remember {
    SearchBarState(
        focusManager = focusManager,
        isSearchIconVisible = isSearchIconVisible,
        isCloseIconVisible = isCloseIconVisible
    )
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    state: SearchBarState = rememberSearchBarState(),
    textFieldColors:TextFieldColors = TextFieldDefaults.textFieldColors(),
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
                contentDescription = "Close Icon",
                tint = MaterialTheme.colors.primary
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = textFieldColors,
    )

}

