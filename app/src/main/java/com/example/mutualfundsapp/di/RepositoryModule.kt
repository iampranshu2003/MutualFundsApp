package com.example.mutualfundsapp.di

import com.example.mutualfundsapp.domain.repository.MututalFundsRepository
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
    abstract fun bindMfRepository(impl: MfRepositoryImpl): MututalFundsRepository

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository
}
