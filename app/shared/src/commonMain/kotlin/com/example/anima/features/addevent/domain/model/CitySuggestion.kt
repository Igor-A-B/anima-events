package com.example.anima.features.addevent.domain.model

/**
 * A city the exhibitor can attach to an event's address.
 *
 * Mirrors the city fields on [com.example.anima.features.address.models.Address]
 * (cityName/stateName/countryCode/latitude/longitude) on purpose: whatever fills
 * this type later — GeoNames, another provider — plugs in without touching the
 * draft, the ViewModel or the form. Only [com.example.anima.features.addevent.data.MockCityCatalog]
 * gets replaced.
 */
data class CitySuggestion(
    val cityName: String,
    val stateName: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
) {
    val displayName: String get() = "$cityName, $stateName"
}
