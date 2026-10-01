package com.example.anima.di

import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.events.EventChanges
import com.example.anima.core.image.ImageUploader
import com.example.anima.core.network.createApiClient
import com.example.anima.core.network.createPlainClient
import com.example.anima.features.addevent.data.ApiExhibitorEventRepository
import com.example.anima.features.addevent.data.ExhibitorEventRepository
import com.example.anima.features.addevent.presentation.AddEventViewModel
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.features.auth.data.ApiAuthRepository
import com.example.anima.features.auth.data.AuthRepository
import com.example.anima.features.auth.data.SettingsTokenStorage
import com.example.anima.features.auth.data.TokenStorage
import com.example.anima.features.auth.presentation.login.LoginViewModel
import com.example.anima.features.auth.presentation.register.RegisterViewModel
import com.example.anima.features.eventdetail.data.ApiEventImageRepository
import com.example.anima.features.eventdetail.data.EventImageRepository
import com.example.anima.features.eventdetail.presentation.EventDetailViewModel
import com.example.anima.features.feed.data.ApiFeedRepository
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.presentation.FeedViewModel
import com.example.anima.features.subscription.data.ApiSubscriptionRepository
import com.example.anima.features.subscription.data.SubscriptionRepository
import com.example.anima.features.profile.data.repository.ApiProfileRepository
import com.example.anima.features.profile.domain.repository.ProfileRepository
import com.example.anima.features.profile.presentation.ProfileViewModel
import com.example.anima.features.search.data.ApiSearchRepository
import com.example.anima.features.search.data.SearchRepository
import com.example.anima.features.search.presentation.SearchViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppExceptionBus() }
    single { EventChanges() }
    single<TokenStorage> { SettingsTokenStorage() }
    // login and refresh use the client without the auth plugin
    single<AuthRepository> { ApiAuthRepository(createPlainClient()) }
    single { SessionRepository(get(), get()) }
    single<HttpClient> { createApiClient(get<SessionRepository>()) }

    single<FeedRepository> { ApiFeedRepository(get()) }
    single<SearchRepository> { ApiSearchRepository(get()) }
    single<SubscriptionRepository> { ApiSubscriptionRepository(get()) }
    single<ProfileRepository> { ApiProfileRepository(get()) }
    single<ExhibitorEventRepository> { ApiExhibitorEventRepository(get(), get()) }
    single { ImageUploader(get()) }
    single<EventImageRepository> { ApiEventImageRepository(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { FeedViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { EventDetailViewModel(get(), get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { AddEventViewModel(get(), get(), get()) }
}
