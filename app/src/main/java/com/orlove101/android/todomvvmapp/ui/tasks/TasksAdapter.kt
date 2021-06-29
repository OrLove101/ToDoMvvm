package com.orlove101.android.todomvvmapp.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orlove101.android.todomvvmapp.data.models.Task
import com.orlove101.android.todomvvmapp.databinding.ItemTaskBinding

class TasksAdapter(private val listener: OnItemClickListener): ListAdapter<Task,
        TasksAdapter.TasksViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TasksViewHolder(private val binding: ItemTaskBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    val task = getItem(position)

                    if ( position != RecyclerView.NO_POSITION ) {
                        listener.onItemClick(task)
                    }
                }
                checkBox.setOnClickListener {
                    val position = adapterPosition
                    val task = getItem(position)

                    if ( position != RecyclerView.NO_POSITION ) {
                        listener.onCheckBoxClick(task, checkBox.isChecked)
                    }
                }
            }
        }

            fun bind(item: Task) {
                binding.apply {
                    checkBox.isChecked = item.completed
                    textView.text = item.name
                    textView.paint.isStrikeThruText = item.completed
                    exclaimImage.isVisible = item.important
                }
            }
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback: DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}