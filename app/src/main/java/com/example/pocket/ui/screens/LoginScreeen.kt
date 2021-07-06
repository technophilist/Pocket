package com.example.pocket.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun LoginScreen(navController: NavController) {

    var emailAddressText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

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
            textStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.padding(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(),
            value = passwordText,
            onValueChange = { passwordText = it },
            placeholder = { Text(text = "Password") },
            textStyle = MaterialTheme.typography.body1
        )

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
            onClick = { },
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Log in", fontWeight = FontWeight.Bold)
        }

    }

}