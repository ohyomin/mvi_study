package com.example.mvvmtest.view.addedittask

sealed class AddEditTaskViewEvent {
    data class TitleChange(val title: String): AddEditTaskViewEvent()
    data class DescriptionChange(val description: String): AddEditTaskViewEvent()
    object SaveTaskClick: AddEditTaskViewEvent()
    object DeleteTAskClick: AddEditTaskViewEvent()
    object CancelTaskClick: AddEditTaskViewEvent()
}
