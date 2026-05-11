package com.example.teqnotes.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqnotes.components.CollabButton
import com.example.teqnotes.components.DividerText
import com.example.teqnotes.components.TeqnotesLogo
import com.example.teqnotes.ui.theme.ButtonBg
import com.example.teqnotes.ui.theme.Secondary

import com.example.teqnotes.ui.theme.Typography

@Composable
fun WelcomeScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Добро пожаловать!",
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lorem ipsum dolor sit amet consectetur adipiscing elit. " +
                        "Sit amet consectetur adipiscing elit quisque faucibus ex. " +
                        "Adipiscing elit quisque faucibus ex sapien vitae pellentesque.",
                style = Typography.bodyLarge.copy(
                    lineHeight = 24.sp
                ),
                color = Secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            CollabButton(
                text = "Зарегистрироваться",
                onClick = onRegisterClick,
                backgroundColor = ButtonBg,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            DividerText(
                text = "или",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CollabButton(
                text = "Войти",
                onClick = onLoginClick,
                backgroundColor = Color.Transparent,
                borderWidth = 4f,
                borderColor = ButtonBg,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onRegisterClick = { /* mock */ },
        onLoginClick = { /* mock */ }
    )
}