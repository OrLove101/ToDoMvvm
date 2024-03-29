package com.orlove101.android.todomvvmapp.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orlove101.android.todomvvmapp.data.models.Task
import com.orlove101.android.todomvvmapp.data.persistence.TaskDao
import com.orlove101.android.todomvvmapp.ui.ADD_TASK_RESULT_OK
import com.orlove101.android.todomvvmapp.ui.EDIT_TASK_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
    // (contain little pieces information to recreate out ui for onSavedState) OR (stored navigation arguments)
): ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskName", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if ( taskName.isBlank() ) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updateTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updateTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun createTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }
    }

    private fun showInvalidInputMessage(text: String) {
        viewModelScope.launch {
            addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
        }
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String): AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int): AddEditTaskEvent()
    }
}