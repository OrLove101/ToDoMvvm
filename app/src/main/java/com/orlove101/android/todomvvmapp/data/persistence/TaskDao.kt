package com.orlove101.android.todomvvmapp.data.persistence

import androidx.room.*
import com.orlove101.android.todomvvmapp.data.models.Task
import kotlinx.coroutines.flow.Flow



@Dao //Data access object (here we declare methods that we will do with our database)
interface TaskDao {

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name") // || - in SQL is append operator to concatenate strings // '%' on start ant at the end means that no matter where in sentence our query
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>> // Flow - (kotlinx.coroutines.flow.Flow) represent a asynchronous stream of data, only used inside the coroutine

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task) // suspend fun - function which do in different thread, can become only in another suspend function or in coroutine

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}