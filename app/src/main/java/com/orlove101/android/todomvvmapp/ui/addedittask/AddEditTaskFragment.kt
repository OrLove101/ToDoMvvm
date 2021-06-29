package com.orlove101.android.todomvvmapp.ui.addedittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.orlove101.android.todomvvmapp.databinding.FragmentEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEditTaskFragment: Fragment() {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            editTextTextPersonName.setText(viewModel.taskName)
            checkBox.isChecked = viewModel.taskImportance
            checkBox.jumpDrawablesToCurrentState()
            textView.isVisible = viewModel.task != null
            textView.text = "Created: ${viewModel.task?.createdDateFormatted}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}