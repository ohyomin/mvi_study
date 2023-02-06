package com.example.mvvmtest.intent

import com.example.mvvmtest.model.Task
import com.example.mvvmtest.model.TaskEditorModelStore
import com.example.mvvmtest.model.TaskEditorState
import com.example.mvvmtest.model.TaskEditorState.Closed
import com.example.mvvmtest.model.TaskEditorState.Editing
import com.example.mvvmtest.model.TasksModelStore
import com.example.mvvmtest.view.addedittask.AddEditTaskViewEvent
import com.example.mvvmtest.view.addedittask.AddEditTaskViewEvent.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddEditTaskIntentFactory @Inject constructor(
    private val taskEditorModelStore: TaskEditorModelStore,
    private val tasksModelStore: TasksModelStore
) {
    fun process(viewEvent: AddEditTaskViewEvent) {
        taskEditorModelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: AddEditTaskViewEvent): Intent<TaskEditorState> {
        return when(viewEvent) {
            is TitleChange -> buildEditTitleIntent(viewEvent)
            is DescriptionChange -> buildEditDescriptionIntent(viewEvent)
            SaveTaskClick  -> buildSaveIntent()
            DeleteTAskClick -> buildDeleteIntent()
            CancelTaskClick -> buildCancelIntent()
        }
    }

    private fun buildSaveIntent() = editorIntent<Editing> {
        save().run {
            val intent = TasksIntentFactory.buildAddOrUpdateTaskIntent(task)
            tasksModelStore.process(intent)
            saved()
        }
    }

    private fun buildDeleteIntent() = editorIntent<Editing> {
        delete().run {
            val intent = TasksIntentFactory.buildDeleteTaskIntent(taskId)
            tasksModelStore.process(intent)
            deleted()
        }
    }


    companion object {
        inline fun <reified S : TaskEditorState> editorIntent(
            crossinline block: S.() -> TaskEditorState
        ) : Intent<TaskEditorState> {
            return intent {
                (this as? S)?.block()
                    ?: throw IllegalAccessError()
            }
        }

        fun buildAddTaskIntent(task: Task) = editorIntent<Closed> { addTask(task) }

        fun buildEditTaskIntent(task: Task) = editorIntent<Closed> { editTask(task) }

        private fun buildEditTitleIntent(viewEvent: TitleChange) = editorIntent<Editing> {
            edit { copy(title = viewEvent.title) }
        }

        private fun buildEditDescriptionIntent(viewEvent: DescriptionChange) = editorIntent<Editing> {
            edit { copy(description = viewEvent.description) }
        }

        private fun buildCancelIntent() = editorIntent<Editing> { cancel() }
    }
}