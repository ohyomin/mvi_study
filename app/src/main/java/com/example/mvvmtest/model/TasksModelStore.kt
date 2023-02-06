package com.example.mvvmtest.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksModelStore @Inject constructor() :
        ModelStore<TasksState>(
            TasksState(
                emptyList(),
                FilterType.ANY,
                SyncState.IDLE,
            )
        )