package com.example.mvvmtest.intent

import com.example.mvvmtest.model.*
import com.example.mvvmtest.model.SyncState.*
import com.example.mvvmtest.model.SyncState.PROCESS.Type.REFRESH
import com.example.mvvmtest.model.backend.TasksRestApi
import com.example.mvvmtest.view.tasks.TasksViewEvent
import com.example.mvvmtest.view.tasks.TasksViewEvent.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksIntentFactory @Inject constructor(
    private val tasksModelStore: TasksModelStore,
    private val taskEditorModelStore: TaskEditorModelStore,
    private val tasksRestApi: TasksRestApi
) {
    fun process(event: TasksViewEvent) {
        //tasksModelStore.process(to)
    }

    private fun toIntent(viewEvent: TasksViewEvent): Intent<TasksState> {
        return when(viewEvent) {
            ClearCompletedClick -> buildClearCompletedIntent()
            FilterTypeClick -> buildCycleFilterIntent()
            RefreshTasksSwipe, RefreshTasksClick -> buildReloadTasksIntent()
            NewTaskClick -> buildNewTaskIntent()
            is CompleteTaskClick -> buildCompleteTaskClick(viewEvent)
            is EditTaskClick -> buildEditTaskIntent(viewEvent)
        }
    }

    private fun buildEditTaskIntent(viewEvent: EditTaskClick): Intent<TasksState> {
        return sideEffect {
            val intent = AddEditTaskIntentFactory.buildAddTaskIntent(viewEvent.task)
            taskEditorModelStore.process(intent)
        }
    }

    private fun buildNewTaskIntent(): Intent<TasksState> = sideEffect {
        val addIntent = AddEditTaskIntentFactory.buildAddTaskIntent(Task())
        taskEditorModelStore.process(addIntent)
    }

    private fun buildCompleteTaskClick(viewEvent: TasksViewEvent.CompleteTaskClick): Intent<TasksState> {
        return intent {
            val mutableList = tasks.toMutableList()
            mutableList[tasks.indexOf(viewEvent.task)] =
                viewEvent.task.copy(completed = viewEvent.checked)
            copy(tasks = mutableList)
        }
    }

    private fun buildClearCompletedIntent(): Intent<TasksState> {
        return intent {
            copy(tasks = tasks.filter { !it.completed }.toList())
        }
    }

    private fun buildCycleFilterIntent(): Intent<TasksState> {
        return intent {
            copy( filter = when(filter) {
                FilterType.ANY -> FilterType.ACTIVE
                FilterType.ACTIVE -> FilterType.COMPLETE
                FilterType.COMPLETE -> FilterType.ANY
            })
        }
    }

    private fun chainedIntent(block: TasksState.() -> TasksState) {
        tasksModelStore.process(intent(block))
    }

    private fun buildReloadTasksIntent(): Intent<TasksState> {
        return intent {
            fun retrofitSuccess(loadedTasks: List<Task>) = chainedIntent {
                copy(tasks = loadedTasks, syncState = IDLE)
            }

            fun retrofitError(throwable: Throwable) = chainedIntent {
                copy(syncState = ERROR(throwable))
            }

            val disposable = tasksRestApi.getTasks()
                .map { it.values.toList() }
                .subscribeOn(Schedulers.io())
                .subscribe(::retrofitSuccess, ::retrofitError)

            copy(syncState = PROCESS(REFRESH, disposable::dispose))
        }
    }

    companion object {
        fun buildAddOrUpdateTaskIntent(task: Task) : Intent<TasksState> = intent {
            tasks.toMutableList().let { newList ->
                newList.find { task.id  == it.id }?.let {
                    newList[newList.indexOf(it)] = task
                } ?: newList.add(task)
                copy(tasks = newList)
            }
        }

        fun buildDeleteTaskIntent(taskId: String): Intent<TasksState> = intent {
            copy(tasks = tasks.toMutableList().apply {
                find { it.id == taskId }?.also { remove(it) }
            })
        }
    }
}