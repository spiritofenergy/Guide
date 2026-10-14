package com.kodex.guide.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kodex.bookmarketcompose.R
import com.kodex.guide.ui.dialods.SettingsRestartDialog
import com.kodex.guide.presentation.navigation.NavRoutes

@Composable
fun LoginScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigationToMainScreen: (NavRoutes.HomeDataObject) -> Unit,
    onNavigationToSignUpScreen: (NavRoutes.SingUpNavObject) -> Unit
) {


    LaunchedEffect(key1 = Unit) {
        viewModel.getAccountState()
        viewModel.getEmail()
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveLastEmail()
            viewModel.passwordState.value = ""
        }
    }
    Image(
        painter = painterResource(
            id = R.drawable.bereg
        ),
        contentDescription = "BG",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,

        )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(46.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.taman),
            color = Color.White,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = stringResource(R.string.guide),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (viewModel.currentUser.value == null) {
            RoundedCornerTextField(
                text = viewModel.emailState.value,
                label = stringResource(R.string.login),
                isPassword = false
            ) {
                viewModel.emailState.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (!viewModel.resetPasswordState.value) {
                RoundedCornerTextField(
                    text = viewModel.passwordState.value,
                    label = stringResource(R.string.password),
                    isPassword = true
                ) {
                    viewModel.passwordState.value = it
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
            if (viewModel.errorState.value.isNotEmpty()) {
                Text(
                    text = viewModel.errorState.value,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (!viewModel.resetPasswordState.value) {
                LoginButton(
                    text = stringResource(R.string.enter)
                ) {
                      viewModel.signIn(
                          onSignInSuccess = { navData ->
                              onNavigationToMainScreen(navData)
                          }
                      )
                }
            }
            LoginButton(
                text = if (viewModel.resetPasswordState.value) {
                    stringResource(R.string.recover_the_password)
                } else {
                    stringResource(R.string.authorization)
                }
            ) {
                if (viewModel.resetPasswordState.value) {
                    viewModel.resetPassword()
                } else {
                    onNavigationToSignUpScreen(NavRoutes.SingUpNavObject)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

                Text(
                    modifier = Modifier.clickable {
                        viewModel.errorState.value = ""
                        viewModel.resetPasswordState.value = true
                    },
                    text = stringResource(R.string.remind_password),
                    color = Color.White
                )

        } else {
            Spacer(modifier = Modifier.height(10.dp))
            LoginButton(text = stringResource(R.string.enter)) {
                onNavigationToMainScreen(
                    NavRoutes.HomeDataObject(
                        viewModel.currentUser.value!!.uid,
                        viewModel.currentUser.value!!.email!!
                    )
                )
            }
            LoginButton(text = stringResource(R.string.exit)) {
                viewModel.signOut()
            }
        }
        SettingsRestartDialog(
            showDialog = viewModel.showResetPasswordDialog.value,
            onDismiss = {
                viewModel.showResetPasswordDialog.value = false
            },
            onConfirm = {
                viewModel.showResetPasswordDialog.value = false
            },
            message = stringResource(R.string.reset_password_dialog),
        )
    }
}
