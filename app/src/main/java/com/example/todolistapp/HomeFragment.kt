package com.example.todolistapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class HomeFragment : Fragment(), TaskItemClickListener {
    private val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskItemModelFactory((requireActivity().application as TodoApplication).repository)
    }

    private lateinit var taskListAdapter: TaskItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        taskListAdapter = TaskItemAdapter(emptyList(), this)
        binding.taskListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskListAdapter
        }

        // Observe tasks from the ViewModel
        taskViewModel.taskItems.observe(viewLifecycleOwner) { tasks ->
            taskListAdapter.updateItems(tasks)
        }

        // New Task button
        binding.newTaskButton.setOnClickListener {
            try {
                NewTaskSheet(null).show(requireActivity().supportFragmentManager, "newTaskTag")
            } catch (e: Exception) {
                Log.e(TAG, "Error showing NewTaskSheet: ${e.message}")
                // Handle the error appropriately (e.g., show a toast message)
            }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        try {
            NewTaskSheet(taskItem).show(requireActivity().supportFragmentManager, "newTaskTag")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing NewTaskSheet (edit): ${e.message}")
            // Handle the error
        }
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setCompleted(taskItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
