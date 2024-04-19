package com.example.todolistapp

import android.content.Context
import androidx.core.content.ContextCompat
import java.time.LocalTime
import java.util.UUID

class TaskItem (
    var name: String,
    var desc: String,
    var dueTime: LocalTime?,
    var completedDate: LocalTime?,
    var id: UUID = UUID.randomUUID()

)
{
    fun isCompleted() = completedDate != null
    fun imageResource(): Int = if(isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24
    fun imageColor(context: Context): Int =  if(isCompleted()) purple(context) else black(context)

    private fun purple(context: Context) = ContextCompat.getColor(context, com.google.android.material.R.color.material_blue_grey_800)
    private fun black(context: Context) = ContextCompat.getColor(context, R.color.black)

}