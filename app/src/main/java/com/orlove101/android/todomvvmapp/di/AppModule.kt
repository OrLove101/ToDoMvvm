package com.orlove101.android.todomvvmapp.di

import android.app.Application
import androidx.room.Room
import com.orlove101.android.todomvvmapp.data.persistence.TaskDao
import com.orlove101.android.todomvvmapp.data.persistence.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module // here we tell dagger how to create our objects
@InstallIn(SingletonComponent::class)// to use throughout hole app (without this could be created only within activity)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application, // to take context automatically
        callback: TaskDatabase.Callback
    ): TaskDatabase =
        Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration() // instruction what we should to do when we updating scheme but didn't declare a proper may crashing strategy (in this case just create new one)
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao = db.taskDao() // singleton automatically

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob()) // coroutine cancels when at list one of his child crashed to avoid this we use SupervisorJob()
}

@Retention(AnnotationRetention.RUNTIME) // create this annotation if have 2 or more coroutine scopes
@Qualifier
annotation class ApplicationScope