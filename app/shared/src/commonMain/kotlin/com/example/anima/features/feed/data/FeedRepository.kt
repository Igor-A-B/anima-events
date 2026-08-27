package com.example.anima.features.feed.data

import com.example.anima.features.feed.domain.FeedSection

// feed data contract
// TODO: swap the implementation to go from mock to API
interface FeedRepository {
    suspend fun getSections(): List<FeedSection>
}
