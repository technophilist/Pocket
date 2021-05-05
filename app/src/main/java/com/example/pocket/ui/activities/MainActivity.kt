package com.example.pocket.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.ui.screens.UrlCard
import com.example.pocket.ui.theme.PocketAppTheme
import com.example.pocket.viewmodels.MainScreenViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var mViewModel: MainScreenViewModel

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        setContent {
            PocketAppTheme {
                HomeScreen(mViewModel)
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun HomeScreen(viewModel: MainScreenViewModel) {
        val urlItems by viewModel.savedUrls.observeAsState()
        val filteredList by viewModel.filteredList.observeAsState()
        var searchText by remember { mutableStateOf("") }
        var isSearchTextBoxEnabled by remember { mutableStateOf(true) }


        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                modifier = Modifier
                    .offset()
                    .fillMaxWidth()
                    .padding(8.dp),
                value = searchText,
                onValueChange = {
                    searchText = it
                    lifecycleScope.launchWhenStarted {
                        viewModel.filter(it)
                    }
                },
                label = { Text(text = "Search...") },
                leadingIcon = { Icon(Icons.Filled.Search, "Search Icon") },
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable {
                            if (isSearchTextBoxEnabled) {
                                searchText = ""
                                isSearchTextBoxEnabled = false
                            }
                        },
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close Icon"
                    )
                },
                enabled = isSearchTextBoxEnabled,
            )
            UrlList(
                urlItems = if (searchText.isBlank()) urlItems ?: listOf()
                else filteredList ?: listOf(),
                onClickItem = { clickedItem -> openUrl(clickedItem.url) }
            )
        }
    }

    @ExperimentalMaterialApi
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

    private fun openUrl(urlString: String) {
        val uri = Uri.parse(urlString)
        val openLinkIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(openLinkIntent)
    }
}




