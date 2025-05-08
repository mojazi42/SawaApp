package com.example.sawaapplication.core.di

import com.example.sawaapplication.core.sharedPreferences.AuthInterceptor
import com.example.sawaapplication.core.sharedPreferences.OkHTTPBuilder
import com.example.sawaapplication.core.sharedPreferences.TokenProvider
import com.example.sawaapplication.screens.authentication.data.dataSources.remote.FirebaseAuthDataSource
import com.example.sawaapplication.screens.authentication.data.repository.AuthRepositoryImpl
import com.example.sawaapplication.screens.authentication.domain.repository.AuthRepository
import com.example.sawaapplication.screens.communities.data.dataSources.remote.CommunityRemoteDataSource
import com.example.sawaapplication.screens.communities.data.repository.CommunityRepositoryImpl
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import com.example.sawaapplication.screens.communities.domain.useCases.GetUserCreatedCommunitiesUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
    fun provideCommunityRemoteDataSource(
        firestore: FirebaseFirestore
    ): CommunityRemoteDataSource {
        return CommunityRemoteDataSource(firestore)
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideCommunityRepository(
        remoteDataSource: CommunityRemoteDataSource
    ): CommunityRepository {
        return CommunityRepositoryImpl(remoteDataSource)
    }

    // Provide GetUserCreatedCommunitiesUseCase
    @Provides
    fun provideGetUserCreatedCommunitiesUseCase(
        communityRepository: CommunityRepository
    ): GetUserCreatedCommunitiesUseCase {
        return GetUserCreatedCommunitiesUseCase(communityRepository)
    }

    @Provides
    fun provideAuthRepository( // AuthRepository
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthRepository = AuthRepositoryImpl(firebaseAuthDataSource)

    @Provides
    fun provideTokenProvider(
        firebaseAuth: FirebaseAuth
    ): TokenProvider = TokenProvider(firebaseAuth)

    @Provides
    fun provideAuthInterceptor(
        tokenProvider: TokenProvider
    ): AuthInterceptor = AuthInterceptor(tokenProvider)

    @Provides
    fun provideOkHTTPBuilder(
        authInterceptor: AuthInterceptor
    ): OkHTTPBuilder = OkHTTPBuilder(authInterceptor)
}
