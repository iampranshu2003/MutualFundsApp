package com.example.mutualfundsapp.di

import com.example.mutualfundsapp.data.repository.MfRepositoryImpl
import com.example.mutualfundsapp.data.repository.WatchlistRepositoryImpl
import com.example.mutualfundsapp.domain.repository.MutualFundsRepository
import com.example.mutualfundsapp.domain.repository.WatchlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMfRepository(impl: MfRepositoryImpl): MutualFundsRepository

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository
}
