package com.example.note_app_kotllin.ui.screens.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.AppDurations
import com.example.note_app_kotllin.core.constants.BorderRadiuses
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.constants.Spaces
import com.example.note_app_kotllin.core.navigation.Home
import com.example.note_app_kotllin.core.navigation.Login
import com.example.note_app_kotllin.core.navigation.Notes
import com.example.note_app_kotllin.core.navigation.Register
import com.example.note_app_kotllin.ui.screens.auth.components.CustomTextField
import com.example.note_app_kotllin.ui.screens.auth.components.RichText
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoginScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: LoginViewModel = hiltViewModel()
) {
    var emailTextInput by remember { mutableStateOf("test@gmail.com") }
    var passwordTextInput by remember { mutableStateOf("Test@1234") }
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val loginSuccessMessage = stringResource(R.string.login_success)

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackBarHostState.showSnackbar(loginSuccessMessage)
            delay(AppDurations.ShortPlus.milliseconds)
            navController.navigate(Home) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (state.isSuccess) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.errorContainer,
                    contentColor = if (state.isSuccess) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onErrorContainer,
                    shape = RoundedCornerShape(BorderRadiuses.Medium)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }) {
            Spacer(modifier = Modifier.height(Spaces.TopSpace))

            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(Paddings.Medium)
            )

            Spacer(modifier = Modifier.height(Spaces.LargeMinus))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Paddings.Large)
            ) {

                CustomTextField(
                    value = emailTextInput,
                    onValueChange = { emailTextInput = it },
                    label = stringResource(R.string.email),
                    placeholder = stringResource(R.string.enter_email),
                    errorMessage = state.emailError

                )
                CustomTextField(
                    value = passwordTextInput,
                    onValueChange = { passwordTextInput = it },
                    label = stringResource(R.string.password),
                    placeholder = stringResource(R.string.enter_password),
                    errorMessage = state.passwordError
                )

                Spacer(modifier = Modifier.height(Spaces.LargeMinus))

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth().height(70.dp)
                        .padding(Paddings.Tiny), onClick = {
                        viewModel.login(emailTextInput, passwordTextInput)

                    }) {
                  if(  state.isLoading){
                      CircularProgressIndicator()
                  }else{
                      Text(
                          text = stringResource(R.string.login),
                          style = MaterialTheme.typography.bodyLarge
                      )
                  }

                }
                Spacer(modifier = Modifier.height(Spaces.LargeMinus))

                RichText(
                    startText = stringResource(R.string.dont_have_account),
                    clickableText = stringResource(R.string.register),
                    onLinkClicked = { navController.navigate(Register) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(Paddings.Tiny)
                )
            }
        }
    }
}