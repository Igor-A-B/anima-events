package com.example.anima.di

import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.network.createApiClient
import com.example.anima.core.network.createPlainClient
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.features.auth.data.ApiAuthRepository
import com.example.anima.features.auth.data.AuthRepository
import com.example.anima.features.auth.data.SettingsTokenStorage
import com.example.anima.features.auth.data.TokenStorage
import com.example.anima.features.auth.presentation.login.LoginViewModel
import com.example.anima.features.auth.presentation.register.RegisterViewModel
import com.example.anima.features.eventdetail.presentation.EventDetailViewModel
import com.example.anima.features.feed.data.ApiFeedRepository
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.presentation.FeedViewModel
import com.example.anima.features.subscription.data.ApiSubscriptionRepository
import com.example.anima.features.subscription.data.SubscriptionRepository
import com.anima.features.user.models.AccountType
import com.example.anima.features.profile.data.repository.MockProfileRepository
import com.example.anima.features.profile.presentation.ProfileViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppExceptionBus() }
    single<TokenStorage> { SettingsTokenStorage() }
    // login and refresh use the client without the auth plugin
    single<AuthRepository> { ApiAuthRepository(createPlainClient()) }
    single { SessionRepository(get(), get()) }
    single<HttpClient> { createApiClient(get<SessionRepository>()) }

    single<FeedRepository> { ApiFeedRepository(get()) }
    single<SubscriptionRepository> { ApiSubscriptionRepository(get()) }

    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { EventDetailViewModel(get(), get(), get()) }
    // TODO: profile is still mock data, only the account type comes from the session
    viewModel {
        val accountType = get<SessionRepository>().session.value?.accountType ?: AccountType.VISITOR
        ProfileViewModel(MockProfileRepository(accountType))
    }
}
