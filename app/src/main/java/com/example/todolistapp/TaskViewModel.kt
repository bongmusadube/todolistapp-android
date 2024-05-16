package com.example.todolistapp

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class TaskViewModel(private val repository: TaskItemRepository): ViewModel()
{
    val taskItems: LiveData<List<TaskItem>> = repository.allTaskItems.asLiveData()

    fun addTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.insertTaskItem(taskItem)


    }

    fun updateTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.updateTaskItem(taskItem)
    }

    fun setCompleted(taskItem: TaskItem) = viewModelScope.launch {
        if (!taskItem.isCompleted())
            taskItem.completedDateString = TaskItem.dateFormatter.format(LocalDate.now())
        repository.updateTaskItem(taskItem)
    }

    fun scheduleTaskAlarm(context: Context, taskItem: TaskItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Construct the intent with task details
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("taskId", taskItem.id)
            putExtra("taskName", taskItem.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, taskItem.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Schedule the alarm
        val triggerDateTime = LocalDateTime.of(taskItem.dueDate(), taskItem.dueTime())
        val triggerMillis = triggerDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
    }






}

class TaskItemModelFactory(private val repository: TaskItemRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}