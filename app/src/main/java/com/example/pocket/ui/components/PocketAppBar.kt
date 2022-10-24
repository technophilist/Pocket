package com.example.pocket.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.pocket.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketAppBar(
    onSearchIconClicked: (() -> Unit)? = null,
    onDropDownMenuIconClicked: (() -> Unit)? = null,
    dropDownMenuContent: @Composable (() -> Unit)? = null,
    onDropDownMenuDismissRequest: () -> Unit,
    isDropDownMenuExpanded: Boolean,
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = { onSearchIconClicked?.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Button"
                )
            }
            Box {
                IconButton(onClick = { onDropDownMenuIconClicked?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu Button",
                    )
                }
                DropdownMenu(
                    expanded = isDropDownMenuExpanded,
                    onDismissRequest = onDropDownMenuDismissRequest,
                    content = { dropDownMenuContent?.invoke() }
                )

            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}