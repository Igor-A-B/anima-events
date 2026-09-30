package com.anima.features.event.models

// search filters, sent as query params to GET /events

// price bucket, FREE means no ticket price at all
enum class PriceFilter {
    ANY,
    FREE,
    PAID,
}

// when the event happens, resolved against the server clock
enum class DateFilter {
    ANY,
    NOW,
    TODAY,
    TOMORROW,
    WEEKEND,
}
