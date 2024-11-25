package com.example.scholaadmin

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DepartmentAdapter(
    private val departments: List<Dept>,
    private val onDeleteClick: (Dept) -> Unit
) : RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.department_item, parent, false)
        return DepartmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val department = departments[position]
        holder.deptTextView.text = holder.itemView.context.getString(
            R.string.department_display_format,
            department.dept_name,
            department.dept_short_name
        )

        holder.deleteIcon.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, department)
        }
    }

    override fun getItemCount(): Int = departments.size

    private fun showDeleteConfirmationDialog(context: Context, department: Dept) {
        AlertDialog.Builder(context)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the department '${department.dept_name}'?")
            .setPositiveButton("Yes") { _, _ ->
                onDeleteClick(department)
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    inner class DepartmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deptTextView: TextView = itemView.findViewById(R.id.deptTextView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }
}
