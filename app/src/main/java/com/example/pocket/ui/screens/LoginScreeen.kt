package com.example.pocket.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pocket.auth.AuthenticationResult
import com.example.pocket.di.AppContainer
import com.example.pocket.di.LoginContainer
import com.example.pocket.ui.navigation.PocketNavigationDestinations
import com.example.pocket.viewmodels.LoginViewModel

@ExperimentalComposeUiApi
@Composable
fun LoginScreen(
    appContainer: AppContainer,
    navController: NavController
) {

    // viewmodel and livedata
    val viewmodelFactory = remember {
        // start login flow
        /*
         * if the login container is not null, it means that this composable
         * was recalled because of a config change.LoginContainer() can survive
         * config changes because it is defined in the Application class.This check
         * prevents a new instance of LoginContainer being constructed on every
         * config change.
         */
        if (appContainer.loginContainer == null) {
            appContainer.loginContainer = LoginContainer(appContainer.authenticationService)
        }
        appContainer.loginContainer!!.loginViewModelFactory
    }
    val viewmodel: LoginViewModel = viewModel(factory = viewmodelFactory)
    val authenticationResult = viewmodel.authenticationResult.observeAsState()

    // states for text fields
    var emailAddressText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    // states for validation
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    /*
     * isCredentialsValid & isErrorMessageVisible need not be wrapped in
     * rememberSaveable because their state will be controlled by the
     * disposable effect, which uses the value of a live data in the viewmodel.
     * Since the viewmodel survives config changes, the disposable effect
     * will restart with the appropriate value.
     */
    var isCredentialsValid by remember { mutableStateOf(false) }
    var isErrorMessageVisible by remember { mutableStateOf(false) }
    /*
    * isLoginButtonEnabled state need not be wrapped in rememberSaveable
    * because this state will be computed only when its keys change.
    * Since the keys use remember saveable, and this state is not,
    * it will automatically be recomputed after a config change.
    */
    val isLoginButtonEnabled by remember(emailAddressText, passwordText) {
        derivedStateOf { emailAddressText.isNotBlank() && passwordText.isNotEmpty() }
    }

    // state for keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // keyboard actions for the text fields
    val keyboardActions = KeyboardActions(onDone = {
        if (emailAddressText.isNotBlank() && passwordText.isNotEmpty()) {
            keyboardController?.hide()
            isLoading = true
            viewmodel.authenticate(emailAddressText, passwordText)
        }
    })

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

    BackHandler {
        // end login flow
        appContainer.loginContainer = null
        navController.navigateUp()
    }

    DisposableEffect(authenticationResult.value) {
        /*
         * This block will get executed only when authenticationResult.value changes.
         * This prevents side effects and makes sure that this block of code doesn't get
         * executed on every re-composition.
         */
        when (authenticationResult.value) {
            is AuthenticationResult.Success -> {
                isLoading = false
                // end login flow
                appContainer.loginContainer = null
                navController.navigate(PocketNavigationDestinations.HOME_SCREEN) {
                    //if successfully logged in, pop the backstack
                    popUpTo(PocketNavigationDestinations.WELCOME_SCREEN) {
                        inclusive = true
                    }
                }
            }
            is AuthenticationResult.Failure -> {
                isLoading = false
                isCredentialsValid = false
                isErrorMessageVisible = true
            }
        }
        onDispose {}
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
                onValueChange = {
                    /*
                     * if isErrorMessageVisible is set to true then it indicates
                     * a failed login attempt.Remove the error message when the user
                     * is making an edit to the email address text.The prevents the
                     * error message from being displayed when the user is re-typing.
                     */
                    if (isErrorMessageVisible) {
                        isErrorMessageVisible = false
                    }
                    emailAddressText = it

                },
                placeholder = { Text(text = "Email Address") },
                textStyle = MaterialTheme.typography.body1,
                isError = isErrorMessageVisible,
                singleLine = true,
                keyboardActions = keyboardActions
            )

            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                value = passwordText,
                onValueChange = {
                    /*
                     * if isErrorMessageVisible is set to true then it indicates
                     * a failed login attempt.Remove the error message when the user
                     * is making an edit to the password text.The prevents the
                     * error message from being displayed when the user is re-typing.
                     */
                    if (isErrorMessageVisible) {
                        isErrorMessageVisible = false
                    }
                    passwordText = it
                },
                placeholder = { Text(text = "Password") },
                textStyle = MaterialTheme.typography.body1,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = isErrorMessageVisible,
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = ""
                    )
                },
                singleLine = true,
                keyboardActions = keyboardActions
            )

            if (isErrorMessageVisible && !isCredentialsValid) {
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
                content = { Text(text = "Log in", fontWeight = FontWeight.Bold) },
                enabled = isLoginButtonEnabled
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
