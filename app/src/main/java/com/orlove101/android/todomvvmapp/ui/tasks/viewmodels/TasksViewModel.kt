package com.orlove101.android.todomvvmapp.ui.tasks.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.orlove101.android.todomvvmapp.data.persistence.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao
): ViewModel() {
    val tasks = taskDao.getTasks().asLiveData() //Flow very similar to livedata. Flow more flexible on the other hand live data lifecycle aware
}