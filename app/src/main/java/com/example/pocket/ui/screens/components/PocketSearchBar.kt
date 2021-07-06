package com.example.pocket.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

/**
 * State for [SearchBar] composable component.
 * @param focusManager manages the focus of the search bar
 * @param focusRequester used for monitoring the focus state changes
 * @param isSearchIconVisible indicates the visibility of the search icon
 * @param isCloseIconVisible indicates the visibility of the close icon
 */
class SearchBarState(
    val focusManager: FocusManager,
    val focusRequester: FocusRequester = FocusRequester(),
    isSearchIconVisible: Boolean = true,
    isCloseIconVisible: Boolean = false,
) {
    var isSearchIconVisible by mutableStateOf(isSearchIconVisible)
    var isCloseIconVisible by mutableStateOf(isCloseIconVisible)
}

/**
 * Utility function for creating a [SearchBarState] with default
 * behaviour and memoizing it.
 * @param focusManager manages the focus of the search bar
 * @param focusRequester used for monitoring the focus state changes
 * @param isSearchIconVisible indicates the visibility of the search icon
 * @param isCloseIconVisible indicates the visibility of the close icon
 */
@Composable
fun rememberSearchBarState(
    focusManager: FocusManager = LocalFocusManager.current,
    focusRequester: FocusRequester = FocusRequester(),
    isSearchIconVisible: Boolean = true,
    isCloseIconVisible: Boolean = false
) = rememberSaveable {
    SearchBarState(
        focusManager = focusManager,
        focusRequester = focusRequester,
        isSearchIconVisible = isSearchIconVisible,
        isCloseIconVisible = isCloseIconVisible
    )
}

/**
 * SearchBar composable is used for displaying a search bar with some useful defaults.
 *
 * This component displays a search bar with a leading search icon and trailing close icon.
 * It is designed in such a way that both of the icons only appear when they are needed.
 * ie.when the search icon is visible, the close icon wont be visible and vice versa.
 * When the user taps on the search bar, the search icon automatically disappears,the search
 * bar is focused and the close icon appears on the screen.When the close icon is tapped,
 * the focus is cleared and the search icon appears.All default behaviours can be modified
 * using the [SearchBarState] and the method params.
 *
 * @param modifier optional modifier for the search bar
 * @param searchText the text that is to be displayed in the search bar
 * @param keyboardActions used to specify actions that will be triggered in response to users
 * triggering IME action on the software keyboard.
 * @param onCloseIconClicked the action that is to be done when the close icon is clicked
 * @param onFocusChanged the action that is to be done when the focus of the search bar is changed
 * @param onSearchTextChange the action that is to be done when the text in the search bar has
 * changed
 * @param state the search bar state
 * @param textFieldColors colors that will be used to resolve color of the text, content
 * (including label, placeholder, leading and trailing icons, indicator line) and background for
 * this text field in different states
 */
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    state: SearchBarState = rememberSearchBarState(),
    textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    keyboardActions: KeyboardActions = KeyboardActions(onSearch = {
        if (searchText.isBlank()) {
            state.isSearchIconVisible = true
            state.isCloseIconVisible = false
        }
        state.focusManager.clearFocus()
    }),
    onFocusChanged: ((FocusState) -> Unit) = {
        if (it.isFocused) {
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
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = textFieldColors,
    )

}

