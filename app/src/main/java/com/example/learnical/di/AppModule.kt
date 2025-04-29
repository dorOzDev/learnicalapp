package com.example.learnical.di

import com.example.learnical.spotify.SpotifyService
import com.example.learnical.spotify.SpotifyServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindSpotifyService(spotifyServiceImpl: SpotifyServiceImpl): SpotifyService
}

