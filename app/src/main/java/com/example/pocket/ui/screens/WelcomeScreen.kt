package com.example.pocket.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pocket.R
import com.example.pocket.ui.theme.PocketAppTheme

@Composable
fun WelcomeScreen(isDarkMode: Boolean = false) {
    PocketAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = "Welcome To Pocket",
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h1,
            )

            VectorArt(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = { /*TODO*/ },
                content = { Text(text = "Create account") }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
                    .clickable { TODO() },
                text = "Log in",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )

        }
    }
}

@Composable
fun VectorArt(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {

        Image(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.welcome_screen_save_files),
            contentDescription = ""
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = "Save what really interests you",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
            text = "Save articles,videos or any online content you like." +
                    "Add them to Pocket and read them when you have time.",
            textAlign = TextAlign.Center
        )

    }
}
