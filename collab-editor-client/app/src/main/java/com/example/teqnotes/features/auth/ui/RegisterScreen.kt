package com.example.teqnotes.features.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.components.CollabButton
import com.example.teqnotes.core.ui.components.CustomTextField
import com.example.teqnotes.core.ui.components.TeqnotesLogo
import com.example.teqnotes.core.ui.components.bars.TopBar
import com.example.teqnotes.core.ui.theme.FiraCode
import com.example.teqnotes.features.auth.presentation.AuthUiEvent
import com.example.teqnotes.features.auth.presentation.AuthUiState
import com.example.teqnotes.features.auth.presentation.AuthViewModel

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onRegisterSuccess()
            is AuthUiState.Error -> {
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(onBackClick = onBackClick)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TeqnotesLogo()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                leadingIcon = R.drawable.sv_email,
                placeholder = "Почта",
                enabled = uiState !is AuthUiState.Loading,
                isError = uiState is AuthUiState.Error && email.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = nickname,
                onValueChange = { nickname = it },
                leadingIcon = R.drawable.sv_person,
                placeholder = "Никнейм",
                enabled = uiState !is AuthUiState.Loading,
                isError = uiState is AuthUiState.Error && nickname.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                leadingIcon = R.drawable.sv_lock,
                placeholder = "Пароль",
                isPassword = true,
                enabled = uiState !is AuthUiState.Loading,
                isError = uiState is AuthUiState.Error && password.length < 6
            )

            Spacer(modifier = Modifier.height(48.dp))

            CollabButton(
                text = "Зарегистрироваться",
                onClick = {
                    viewModel.onEvent(
                        AuthUiEvent(
                            email = email.trim(),
                            password = password,
                            name = nickname.trim(),
                            type = AuthUiEvent.Type.REGISTER
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AuthUiState.Loading &&
                        email.isNotBlank() &&
                        nickname.isNotBlank() &&
                        password.length >= 6
            )

            (uiState as? AuthUiState.Error)?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error.message,
                    color = MaterialTheme.colorScheme.error,
                    style = TextStyle(
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onBackClick = { /* mock */ },
        onRegisterSuccess = { /* mock */ }
    )
}