package com.example.teqnotes.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqnotes.core.ui.components.bars.SimpleTopBar
import com.example.teqnotes.core.ui.theme.FiraCode


@Composable
fun FaqScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        SimpleTopBar(
            title = "FAQ",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Что такое Teqnotes?")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Teqnotes — это минималистичное клиент-серверное приложение для совместного ведения текстовых заметок. Оно позволяет создавать личные записи, объединять их в проекты и приглашать друзей для совместной работы в реальном времени. Наша цель — сделать организацию мыслей простой, быстрой и доступной на любом устройстве.",
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FiraCode,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        SectionHeader(title = "Roadmap")

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            RoadmapItem(text = "- Поддержка Markdown форматирования")
            RoadmapItem(text = "- Прикрепление изображений к заметкам")
            RoadmapItem(text = "- Голосовые заметки")
            RoadmapItem(text = "- Интеграция с календарем")
            RoadmapItem(text = "- PWA версия для десктопа")
        }

        Spacer(modifier = Modifier.height(32.dp))

        SectionHeader(title = "Support")

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ContactItem(text = "Если остались вопросы:")
            ContactItem(text = "- Email: support@teqnotes.com")
            ContactItem(text = "- GitHub: github.com/teqnotes")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
private fun SectionHeader(title: String) {
    Column {
        Text(
            text = title,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FiraCode,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun RoadmapItem(text: String) {
    Text(
        text = text,
        style = androidx.compose.ui.text.TextStyle(
            fontFamily = FiraCode,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ContactItem(text: String) {
    Text(
        text = text,
        style = androidx.compose.ui.text.TextStyle(
            fontFamily = FiraCode,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        color = MaterialTheme.colorScheme.primary
    )
}