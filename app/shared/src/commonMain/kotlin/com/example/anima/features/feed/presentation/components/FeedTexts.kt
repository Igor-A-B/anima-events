package com.example.anima.features.feed.presentation.components

import androidx.compose.runtime.Composable
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.categories_arts
import anima.app.shared.generated.resources.categories_cinema
import anima.app.shared.generated.resources.categories_cuisine
import anima.app.shared.generated.resources.categories_music
import anima.app.shared.generated.resources.categories_party
import anima.app.shared.generated.resources.categories_sport
import anima.app.shared.generated.resources.categories_tech
import anima.app.shared.generated.resources.categories_theater
import anima.app.shared.generated.resources.feed_section_nearby_subtitle
import anima.app.shared.generated.resources.feed_section_nearby_title
import anima.app.shared.generated.resources.feed_section_now_subtitle
import anima.app.shared.generated.resources.feed_section_now_title
import anima.app.shared.generated.resources.feed_section_participating_subtitle
import anima.app.shared.generated.resources.feed_section_participating_title
import anima.app.shared.generated.resources.feed_section_recommended_subtitle
import anima.app.shared.generated.resources.feed_section_recommended_title
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.feed.domain.FeedSectionType
import org.jetbrains.compose.resources.stringResource

// Translate enums so the domain stay language free
@Composable
fun EventCategory.label(): String = stringResource(
    when (this) {
        EventCategory.MUSIC -> Res.string.categories_music
        EventCategory.ARTS -> Res.string.categories_arts
        EventCategory.CUISINE -> Res.string.categories_cuisine
        EventCategory.PARTY -> Res.string.categories_party
        EventCategory.SPORT -> Res.string.categories_sport
        EventCategory.THEATER -> Res.string.categories_theater
        EventCategory.CINEMA -> Res.string.categories_cinema
        EventCategory.TECH -> Res.string.categories_tech
    }
)

@Composable
fun FeedSectionType.title(): String = stringResource(
    when (this) {
        FeedSectionType.HAPPENING_NOW -> Res.string.feed_section_now_title
        FeedSectionType.NEARBY -> Res.string.feed_section_nearby_title
        FeedSectionType.RECOMMENDED -> Res.string.feed_section_recommended_title
        FeedSectionType.PARTICIPATING -> Res.string.feed_section_participating_title
    }
)

@Composable
fun FeedSectionType.subtitle(): String = stringResource(
    when (this) {
        FeedSectionType.HAPPENING_NOW -> Res.string.feed_section_now_subtitle
        FeedSectionType.NEARBY -> Res.string.feed_section_nearby_subtitle
        FeedSectionType.RECOMMENDED -> Res.string.feed_section_recommended_subtitle
        FeedSectionType.PARTICIPATING -> Res.string.feed_section_participating_subtitle
    }
)
