package com.example.learnical.di

import com.example.learnical.lyrics.LyricsRepository
import com.example.learnical.lyrics.LyricsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindLyricsRepository(repo: LyricsRepositoryImpl): LyricsRepository
}