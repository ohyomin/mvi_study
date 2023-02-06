package com.example.mvvmtest.model

import javax.inject.Inject
import javax.inject.Singleton

// dagger 에 의해 singleton 으로 생성되는 class
@Singleton
class TaskEditorModelStore @Inject constructor() :
    ModelStore<TaskEditorState>(TaskEditorState.Closed)