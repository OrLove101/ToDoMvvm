package com.orlove101.android.todomvvmapp.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.orlove101.android.todomvvmapp.data.models.Task
import com.orlove101.android.todomvvmapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>, // with provider we can get dependencies by lazy - variable will be instantiate when we call it (it needed because have circular dependency (database need callback, callback need database))
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) { // executed first time when we create database
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Wash the dishes"))
                dao.insert(Task("Call mom"))
                dao.insert(Task("Buy groceries", important = true))
                dao.insert(Task("Do the laundry", completed = true))
                dao.insert(Task("Visit grandma"))
                dao.insert(Task("Repair my bike", completed = true))
            }
        }
    }
}