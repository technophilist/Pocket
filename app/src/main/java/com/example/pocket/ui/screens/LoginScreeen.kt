package com.example.pocket.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pocket.auth.AuthenticationResult
import com.example.pocket.ui.navigation.NavigationDestinations
import com.example.pocket.viewmodels.LoginViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LoginScreen(
    viewmodel: LoginViewModel,
    navController: NavController
) {

    val authenticationResult = viewmodel.authenticationResult.observeAsState()
    var emailAddressText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    /*
     * Initially these values will be true in order to not display
     * the error message. It also prevents the text fields from
     * being highlighted in red.
     */
    var isCredentialsValid by remember { mutableStateOf(true) }

    DisposableEffect(authenticationResult.value) {
        /*
         * This block will get executed only when authenticationResult.value changes.
         * This prevents side effects and makes sure that this block of code doesn't get
         * executed on every re-composition.
         */
        when (val result = authenticationResult.value) {
            is AuthenticationResult.Success -> {
                isLoading = false
                navController.navigate(NavigationDestinations.HOME_SCREEN.navigationString) {
                    //if successfully logged in, pop the backstack
                    popUpTo(NavigationDestinations.WELCOME_SCREEN.navigationString) {
                        inclusive = true
                    }
                }
            }
            is AuthenticationResult.Failure -> {
                isLoading = false
                /*
                 * if the credentials are invalid or if the user is not found set
                 * isCredentialsValid to false.
                 */
                if (
                    result.exception is FirebaseAuthInvalidCredentialsException ||
                    result.exception is FirebaseAuthInvalidUserException ||
                    result.exception is IllegalArgumentException
                ) isCredentialsValid = false
            }
        }
        onDispose {
            isLoading = true
        }
    }

    val termsAndConditionText = buildAnnotatedString {
        append("By clicking below you agree to our ")

        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
        append("Terms of Use ")
        pop()

        append("and consent to our ")

        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
        append("Privacy Policy.")
        pop()

    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 184.dp),
                text = "Log in with mail",
                style = MaterialTheme.typography.h1
            )

            Spacer(modifier = Modifier.padding(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                value = emailAddressText,
                onValueChange = { emailAddressText = it },
                placeholder = { Text(text = "Email Address") },
                textStyle = MaterialTheme.typography.body1,
                isError = !isCredentialsValid,
                maxLines = 1
            )

            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                value = passwordText,
                onValueChange = { passwordText = it },
                placeholder = { Text(text = "Password") },
                textStyle = MaterialTheme.typography.body1,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = !isCredentialsValid,
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = ""
                    )
                }
            )

            if (!isCredentialsValid) {
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = "The email address or password that you've entered is incorrect. Please check the credentials.",
                    color = MaterialTheme.colors.error
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 24.dp),
                text = termsAndConditionText,
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    isLoading = true
                    viewmodel.authenticate(emailAddressText, passwordText)
                },
                shape = MaterialTheme.shapes.medium,
                content = { Text(text = "Log in", fontWeight = FontWeight.Bold) }
            )
        }

        if (isLoading) LoadingProgressOverlay(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
        )
    }
}

@Composable
private fun LoadingProgressOverlay(modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = Color.Black
    ) {
        Box {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
