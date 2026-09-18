package com.joybrata.notepad.di

import com.joybrata.notepad.data.repository.NoteRepositoryImpl
import com.joybrata.notepad.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Binds the repository interface to its implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        impl: NoteRepositoryImpl
    ): NoteRepository
}
