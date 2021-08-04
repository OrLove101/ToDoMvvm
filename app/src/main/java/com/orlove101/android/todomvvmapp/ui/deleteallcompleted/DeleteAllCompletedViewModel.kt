package com.orlove101.android.todomvvmapp.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import com.orlove101.android.todomvvmapp.data.persistence.TaskDao
import com.orlove101.android.todomvvmapp.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
):  ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }
}