package com.example.anima.features.addevent.data

import com.example.anima.features.addevent.domain.model.CitySuggestion

/**
 * Stand-in for a real city-search API (GeoNames or similar) — see the note on
 * [CitySuggestion]. A small fixed global list is enough to build and use the
 * picker now; [search] is the only thing a real API replaces.
 */
object MockCityCatalog {

    fun search(query: String): List<CitySuggestion> {
        if (query.isBlank()) return emptyList()

        val needle = query.foldForCitySearch()
        return cities.filter { city -> city.cityName.foldForCitySearch().startsWith(needle) }
    }

    private val cities = listOf(
        CitySuggestion("São Paulo", "SP", "BR", -23.5505, -46.6333),
        CitySuggestion("Rio de Janeiro", "RJ", "BR", -22.9068, -43.1729),
        CitySuggestion("Belo Horizonte", "MG", "BR", -19.9167, -43.9345),
        CitySuggestion("Curitiba", "PR", "BR", -25.4284, -49.2733),
        CitySuggestion("Porto Alegre", "RS", "BR", -30.0346, -51.2177),
        CitySuggestion("Lisboa", "Lisboa", "PT", 38.7223, -9.1393),
        CitySuggestion("Porto", "Porto", "PT", 41.1579, -8.6291),
        CitySuggestion("Madrid", "Madrid", "ES", 40.4168, -3.7038),
        CitySuggestion("Buenos Aires", "Buenos Aires", "AR", -34.6037, -58.3816),
        CitySuggestion("New York", "NY", "US", 40.7128, -74.0060),
        CitySuggestion("Miami", "FL", "US", 25.7617, -80.1918),
        CitySuggestion("London", "England", "GB", 51.5072, -0.1276),
    )
}

// accent folding, so "sao paulo" finds "São Paulo" — same idea as
// features/search/data/EventMatching.kt, kept local to avoid a cross-feature import
// for four lines of code
private const val ACCENTED = "áàâãäéèêëíìîïóòôõöúùûüçñ"
private const val PLAIN = "aaaaaeeeeiiiiooooouuuucn"

private fun String.foldForCitySearch(): String = lowercase()
    .map { char ->
        val index = ACCENTED.indexOf(char)
        if (index >= 0) PLAIN[index] else char
    }
    .joinToString(separator = "")
    .trim()
