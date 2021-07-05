package com.example.pocket.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pocket.R
import com.example.pocket.ui.theme.PocketAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@Composable
fun WelcomeScreen(isDarkMode: Boolean = false) {
    PocketAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
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

                VectorArtCarousel(
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
}

@ExperimentalPagerApi
@Composable
fun VectorArtCarousel(modifier: Modifier = Modifier) {

    val pagerState = rememberPagerState(
        pageCount = 3,
        infiniteLoop = true,
        initialOffscreenLimit = 2,
    )

    val page1 = getVectorArtPage(
        painter = painterResource(id = R.drawable.welcome_screen_save_files),
        title = "Save what really interests you",
        description = "Save articles,videos or any online content you like. " +
                "Add them to Pocket and read them when you have time.",
        imageDescription = ""
    )


    val page2 = getVectorArtPage(
        painter = painterResource(id = R.drawable.welcome_screen_read_peacefully_vector_art),
        title = "Your quiet corner of the internet",
        description = "Pocket saves articles in a clean layout designed for reading-" +
                "no interruptions, no popups-so you can sidestep the internet's noise.",
        imageDescription = ""
    )

    val page3 = getVectorArtPage(
        painter = painterResource(id = R.drawable.welcome_screen_dark_mode_vector_art),
        title = "Reduce eye strain with dark mode",
        description = "Want to dim the lights? Don't let a bright screen mess with your eyes or sleep. " +
                "Turn on dark mode and continue your reading.",
        imageDescription = ""
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {

        HorizontalPager(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> page1()
                1 -> page2()
                2 -> page3()
                else -> throw IllegalStateException()
            }
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.CenterHorizontally),
            pagerState = pagerState
        )

    }
}


@Composable
private fun getVectorArtPage(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    description: String,
    imageDescription: String,
): @Composable () -> Unit = @Composable {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(painter = painter, contentDescription = imageDescription)

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = title,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
            text = description,
            textAlign = TextAlign.Center
        )

    }
}






