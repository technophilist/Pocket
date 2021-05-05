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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
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
        val urlItems = viewModel.savedUrls.observeAsState()
        var searchText by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                modifier = Modifier
                    .offset()
                    .fillMaxWidth()
                    .padding(8.dp),
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text(text = "Search...") }
            )
            urlItems.value?.let {
                UrlList(
                    urlItems = it,
                    onClickItem = { clickedItem -> openUrl(clickedItem.url) }
                )
            }
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

