package com.jionifamily.di

import com.jionifamily.data.repository.AuthRepositoryImpl
import com.jionifamily.data.repository.MissionRepositoryImpl
import com.jionifamily.domain.repository.AuthRepository
import com.jionifamily.domain.repository.MissionRepository
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
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMissionRepository(impl: MissionRepositoryImpl): MissionRepository
}
