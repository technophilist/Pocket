package com.example.pocket.ui.screens

import androidx.annotation.DrawableRes
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
import androidx.navigation.NavController
import com.example.pocket.R
import com.example.pocket.ui.navigation.PocketNavigationDestinations
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

private data class VectorArtCard(
    @DrawableRes val id: Int,
    val title: String,
    val description: String,
    val imageDescription: String
)

@ExperimentalPagerApi
@Composable
fun WelcomeScreen(navController: NavController) {
    val vectorArtCards = listOf(
        VectorArtCard(
            R.drawable.welcome_screen_save_files,
            title = "Save what really interests you",
            description = "Save articles,videos or any online content you like. " +
                    "Add them to Pocket and read them when you have time.",
            imageDescription = ""
        ),
        VectorArtCard(
            id = R.drawable.welcome_screen_read_peacefully_vector_art,
            title = "Your quiet corner of the internet",
            description = "Pocket saves articles in a clean layout designed for reading-" +
                    "no interruptions, no popups-so you can sidestep the internet's noise.",
            imageDescription = ""
        ),
        VectorArtCard(
            id = R.drawable.welcome_screen_dark_mode_vector_art,
            title = "Reduce eye strain with dark mode",
            description = "Want to dim the lights? Don't let a bright screen mess with your eyes or sleep. " +
                    "Turn on dark mode and continue your reading.",
            imageDescription = ""
        )
    )
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
                .fillMaxHeight(0.8f),
            vectorArtCards = vectorArtCards
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = { navController.navigate(PocketNavigationDestinations.SIGNUP_SCREEN) },
            content = { Text(text = "Create account") }
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp)
                .clickable { navController.navigate(PocketNavigationDestinations.LOGIN_SCREEN) },
            text = "Log in",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )

    }
}

@ExperimentalPagerApi
@Composable
private fun VectorArtCarousel(modifier: Modifier = Modifier, vectorArtCards: List<VectorArtCard>) {

    val pagerState = rememberPagerState(
        pageCount = vectorArtCards.size,
        infiniteLoop = true,
        initialOffscreenLimit = 2,
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {

        HorizontalPager(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            state = pagerState,
        ) { page ->
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = vectorArtCards[page].id),
                    contentDescription = vectorArtCards[page].imageDescription
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = vectorArtCards[page].title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp),
                    text = vectorArtCards[page].description,
                    textAlign = TextAlign.Center
                )

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






