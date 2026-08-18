package com.example.anima.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import com.example.anima.theme.AnimaTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.anima.feature.events.presentation.EventCard
import com.example.anima.feature.events.presentation.EventCardUiModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {

    val events = remember {
        listOf(
            EventCardUiModel(
                id = "1",
                title = "Anime Friends",
                location = "São Paulo - SP",
                date = "20 de agosto, às 10h",
                category = "Anime",
            ),
            EventCardUiModel(
                id = "2",
                title = "Festival de Games",
                location = "Campinas - SP",
                date = "25 de agosto, às 14h",
                category = "Games",
            ),
            EventCardUiModel(
                id = "3",
                title = "Feira Medieval",
                location = "Curitiba - PR",
                date = "2 de setembro, às 9h",
                category = "Cultura",
            ),
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Eventos próximos",
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}