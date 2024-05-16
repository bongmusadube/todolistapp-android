 package com.example.todolistapp

 import android.app.DatePickerDialog
 import android.app.TimePickerDialog
 import android.os.Bundle
 import android.text.Editable
 import android.view.LayoutInflater
 import android.view.View
 import android.view.ViewGroup
 import androidx.lifecycle.ViewModelProvider
 import com.example.todolistapp.databinding.FragmentNewTaskSheetBinding
 import com.google.android.material.bottomsheet.BottomSheetDialogFragment
 import java.time.LocalDate
 import java.time.LocalTime
 import java.time.format.DateTimeFormatter
 import java.time.LocalDateTime



 class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment()
 {
     private lateinit var binding: FragmentNewTaskSheetBinding
     private lateinit var taskViewModel: TaskViewModel
     private var dueTime: LocalTime? = null
     private var dueDate: LocalDate? = null

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         val activity = requireActivity()
         binding.datePickerButton.setOnClickListener {
             openDatePicker()
         }

         if (taskItem != null)
         {
             binding.taskTitle.text = "Edit Task"
             val editable = Editable.Factory.getInstance()
             binding.name.text = editable.newEditable(taskItem!!.name)
             binding.desc.text = editable.newEditable(taskItem!!.desc)
             if(taskItem!!.dueTime() != null)
             {
                 dueTime = taskItem!!.dueTime()!!
                 updateTimeButtonText()
             }
             if(taskItem!!.dueDate() != null)
             {
                 dueDate = taskItem!!.dueDate()!!
                 updateDateButtonText()
             }
         }
         else
         {
             binding.taskTitle.text = "New Task"
         }
         binding.datePickerButton.text = dueDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "Select Date"
         taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
         binding.saveButton.setOnClickListener {
             saveAction()
         }
         binding.timePickerButton.setOnClickListener {
             openTimePicker()
         }
     }

     private fun openDatePicker() {
         // If dueDate is null, use today's date as the default
         val defaultDate = dueDate ?: LocalDate.now()

         val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
             dueDate = LocalDate.of(year, month + 1, dayOfMonth)
             updateDateButtonText()
         }

         val dialog = DatePickerDialog(requireContext(), listener, defaultDate.year, defaultDate.monthValue - 1, defaultDate.dayOfMonth)
         dialog.show()
     }

     private fun updateDateButtonText() {
         binding.datePickerButton.text = dueDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "Select Date"
     }
     private fun openTimePicker() {
         if(dueTime == null)
             dueTime = LocalTime.now()
         val listener = TimePickerDialog.OnTimeSetListener{ _, selectedHour, selectedMinute ->
             dueTime = LocalTime.of(selectedHour, selectedMinute)
             updateTimeButtonText()
         }
         val dialog = TimePickerDialog(activity, listener, dueTime!!.hour, dueTime!!.minute, true)
         dialog.setTitle("Task Due")
         dialog.show()

     }

     private fun updateTimeButtonText() {
         binding.timePickerButton.text = String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
     }

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = FragmentNewTaskSheetBinding.inflate(inflater,container,false)
         return binding.root
     }


     private fun saveAction()
     {
         val name = binding.name.text.toString()
         val desc = binding.desc.text.toString()
         val dueTimeString = if(dueTime == null) null else TaskItem.timeFormatter.format(dueTime)
         val reminderTime = if (dueDate != null && dueTime != null) {
             LocalDateTime.of(dueDate, dueTime)
         } else {
             null
         }
         val dueDate = if(dueDate == null) null else TaskItem.dateFormatter.format(dueDate)

         val newTask = TaskItem(name, desc, dueTimeString, dueDate, null)

         if(taskItem == null)
         {
             val newTask = TaskItem(name, desc, dueTimeString, dueDate, null)
             taskViewModel.addTaskItem(newTask)
         }
         else
         {
             taskItem!!.name = name
             taskItem!!.desc = name
             taskItem!!.dueTimeString = dueTimeString
             taskItem!!.dueDate = dueDate


             taskViewModel.updateTaskItem(taskItem!!)
         }

         // Schedule the task alarm
         taskViewModel.scheduleTaskAlarm(requireContext(), taskItem ?: newTask)
         binding.name.setText("")
         binding.desc.setText("")
         dismiss()
     }

 }