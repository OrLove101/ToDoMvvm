package com.orlove101.android.todomvvmapp.ui.tasks

import androidx.lifecycle.*
import com.orlove101.android.todomvvmapp.data.models.Task
import com.orlove101.android.todomvvmapp.data.persistence.PreferencesManager
import com.orlove101.android.todomvvmapp.data.persistence.SortOrder
import com.orlove101.android.todomvvmapp.data.persistence.TaskDao
import com.orlove101.android.todomvvmapp.ui.ADD_TASK_RESULT_OK
import com.orlove101.android.todomvvmapp.ui.EDIT_TASK_RESULT_OK
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
): ViewModel() { // ViewModel live during rotating device but die when process stops

    val searchQuery = state.getLiveData("searchQuery", "")// MutableStateFlow it like mutableLiveData it can hold a single value not like a normal flow that holds a stream of values

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>() // with a channel we could send data between two coroutines
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow,
    ){ query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->// will call it when searchQuery or preferencesFlow is changed
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData() // Flow very similar to livedata. Flow more flexible on the other hand live data lifecycle aware

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }
    }

    fun onTaskSwiped(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
            tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
        }
    }

    fun onUndoDeleteClick(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun onAddNewTaskClick() {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
        }
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick(): Job {
        return viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
        }
    }

    sealed class TasksEvent {
        // if event takes no arguments we could make object class that will be more efficient
        object NavigateToAddTaskScreen: TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task): TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task): TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String): TasksEvent()
        object NavigateToDeleteAllCompletedScreen: TasksEvent()
    }
}