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
import com.example.pocket.auth.AuthServiceInvalidEmailException
import com.example.pocket.auth.AuthServiceInvalidPasswordException
import com.example.pocket.auth.AuthServiceUserCollisionException
import com.example.pocket.auth.AuthenticationResult
import com.example.pocket.di.AppContainer
import com.example.pocket.di.SignUpContainer
import com.example.pocket.ui.navigation.PocketNavigationDestinations
import com.example.pocket.ui.screens.components.CircularLoadingProgressOverlay
import com.example.pocket.viewmodels.SignUpViewModel

@ExperimentalComposeUiApi
@Composable
fun SignUpScreen(
    appContainer: AppContainer,
    navController: NavController
) {

    // viewmodel and livedata
    val viewmodelFactory = remember {
        //start sign-up flow
        /*
         * if the signup container is not null, it means that this composable
         * was recalled because of a config change.SignUpContainer() can survive
         * config changes because it is defined in the Application class.This check
         * prevents a new instance of SignUpContainer being constructed on every
         * config change.
         */
        if (appContainer.signUpContainer == null) {
            appContainer.signUpContainer = SignUpContainer(appContainer.authenticationService)
        }
        appContainer.signUpContainer!!.signUpViewModelFactory
    }
    val viewmodel: SignUpViewModel = viewModel(factory = viewmodelFactory)
    val result = viewmodel.accountCreationResult.observeAsState()

    // states for text fields
    var emailAddressText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var firstNameText by rememberSaveable { mutableStateOf("") }
    var lastNameText by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // states for validation and error messages
    /*
     * isErrorMessageVisible and errorMessage need not be wrapped in
     * rememberSaveable because their state will be controlled by the
     * disposable effect, which uses the value of a live data in the viewmodel.
     * Since the viewmodel survives config changes, the disposable effect
     * will restart with the appropriate value.
     */
    var isErrorMessageVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // state for signup button
    /*
     * isSignUpButtonEnabled need not be wrapped in rememberSaveable
     * because this state will be computed only when its keys change.
     * Since the keys use remember saveable, and this state is not,
     * it will automatically be recomputed to the correct value
     * after a config change based on the keys.
     */
    val isSignUpButtonEnabled by remember(
        firstNameText,
        lastNameText,
        emailAddressText,
        passwordText
    ) {
        derivedStateOf {
            firstNameText.isNotBlank() && lastNameText.isNotBlank() && emailAddressText.isNotBlank() && passwordText.isNotEmpty()
        }
    }

    // state for visibility of loading animation
    var isLoading by rememberSaveable { mutableStateOf(false) }

    // states for keyboard
    val keyboardController = LocalSoftwareKeyboardController.current
    //keyboard action object that is common to all text fields
    val keyboardActions = KeyboardActions(onDone = {
        if (firstNameText.isNotBlank() && lastNameText.isNotBlank() && emailAddressText.isNotBlank() && passwordText.isNotEmpty()) {
            keyboardController?.hide()
            isLoading = true
            viewmodel.createNewAccount(
                "$firstNameText $lastNameText",
                emailAddressText,
                passwordText
            )
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
        //end signup flow
        appContainer.signUpContainer = null
        navController.navigateUp()
    }

    DisposableEffect(result.value) {
        when (val authResult = result.value) {
            is AuthenticationResult.Success -> {
                isLoading = false
                isErrorMessageVisible = false
                //end sign-up flow
                appContainer.signUpContainer = null
                navController.navigate(PocketNavigationDestinations.HOME_SCREEN) {
                    popUpTo(PocketNavigationDestinations.WELCOME_SCREEN) {
                        inclusive = true
                    }
                }
            }
            is AuthenticationResult.Failure -> {
                isLoading = false
                errorMessage = when (authResult.authServiceException) {
                    is AuthServiceInvalidEmailException -> "Please enter a valid email."
                    is AuthServiceInvalidPasswordException -> "The password must be of length 8, and must contain atleast one uppercase and lowercase letter and atleast one digit."
                    is AuthServiceUserCollisionException -> "A user with the same email already exists."
                    else -> "Please enter a valid email and password"
                }
                isErrorMessageVisible = true
            }
        }

        onDispose { isErrorMessageVisible = false }
    }

    CircularLoadingProgressOverlay(isOverlayVisible = isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 184.dp),
                text = "Sign up for a new account",
                style = MaterialTheme.typography.h1
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {

                OutlinedTextField(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.5f),
                    value = firstNameText,
                    onValueChange = { firstNameText = it },
                    placeholder = { Text(text = "First Name") },
                    textStyle = MaterialTheme.typography.body1,
                    singleLine = true,
                    keyboardActions = keyboardActions
                )

                Spacer(modifier = Modifier.padding(8.dp))

                OutlinedTextField(
                    modifier = Modifier.height(56.dp),
                    value = lastNameText,
                    onValueChange = { lastNameText = it },
                    placeholder = { Text(text = "Last Name") },
                    textStyle = MaterialTheme.typography.body1,
                    singleLine = true,
                    keyboardActions = keyboardActions
                )

            }

            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                value = emailAddressText,
                onValueChange = { emailAddressText = it },
                placeholder = { Text(text = "Email Address") },
                textStyle = MaterialTheme.typography.body1,
                singleLine = true,
                keyboardActions = keyboardActions
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

            if (isErrorMessageVisible) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colors.error
                )
            }

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
                    viewmodel.createNewAccount(
                        "$firstNameText $lastNameText",
                        emailAddressText,
                        passwordText
                    )
                },
                shape = MaterialTheme.shapes.medium,
                content = { Text(text = "Sign Up", fontWeight = FontWeight.Bold) },
                enabled = isSignUpButtonEnabled
            )
        }
    }
}