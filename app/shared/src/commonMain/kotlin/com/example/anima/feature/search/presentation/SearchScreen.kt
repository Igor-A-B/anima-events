package com.example.anima.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun SearchEventsScreen(
    modifier: Modifier = Modifier,
) {
    var searchText by remember {
        mutableStateOf("")
    }

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

    val filteredEvents = remember(searchText) {
        if (searchText.isBlank()) {
            events
        } else {
            events.filter { event ->
                event.title.contains(
                    other = searchText,
                    ignoreCase = true,
                ) ||
                        event.category.contains(
                            other = searchText,
                            ignoreCase = true,
                        ) ||
                        event.location.contains(
                            other = searchText,
                            ignoreCase = true,
                        )
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 24.dp,
        ),
    ) {
        item {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Pesquisar por nome, categoria ou local")
                },
                singleLine = true,
            )
        }

        items(
            items = filteredEvents,
            key = { event -> event.id },
        ) { event ->
            EventCard(event = event)
        }

        if (filteredEvents.isEmpty()) {
            item {
                Text(
                    text = "Nenhum evento encontrado.",
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        }
    }
}