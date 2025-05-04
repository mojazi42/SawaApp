package com.example.sawaapplication.core.di

import com.example.sawaapplication.core.sharedPreferences.AuthInterceptor
import com.example.sawaapplication.core.sharedPreferences.OkHTTPBuilder
import com.example.sawaapplication.core.sharedPreferences.TokenProvider
import com.example.sawaapplication.screens.authentication.data.remote.FirebaseAuthDataSource
import com.example.sawaapplication.screens.authentication.data.repository.AuthRepositoryImpl
import com.example.sawaapplication.screens.authentication.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthDataSource = FirebaseAuthDataSource(firebaseAuth)

    @Provides
    fun provideAuthRepository( //AuthRepository
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthRepository = AuthRepositoryImpl(firebaseAuthDataSource)

    @Provides
    fun provideTokenProvider(
        firebaseAuth: FirebaseAuth
    ): TokenProvider = TokenProvider(firebaseAuth)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenProvider: TokenProvider
    ): AuthInterceptor = AuthInterceptor(tokenProvider)

    @Provides
    @Singleton
    fun provideOkHTTPBuilder(
        authInterceptor: AuthInterceptor
    ): OkHTTPBuilder = OkHTTPBuilder(authInterceptor)
}