package com.joybrata.notepad
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Application entry point annotated for Hilt so it can generate the
 * dependency container used across the app.
 */
@HiltAndroidApp
class NotepadApplication : Application()
