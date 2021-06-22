package com.orlove101.android.todomvvmapp.data.persistence

import androidx.room.*
import com.orlove101.android.todomvvmapp.data.models.Task
import kotlinx.coroutines.flow.Flow



@Dao //Data access object (here we declare methods that we will do with our database)
interface TaskDao {

    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<Task>> // Flow - (kotlinx.coroutines.flow.Flow) represent a asynchronous stream of data, only used inside the coroutine

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task) // suspend fun - function which do in different thread, can become only in another suspend function or in coroutine

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}