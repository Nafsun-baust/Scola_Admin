package com.example.scholaadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CourseAdapter(
    private val courseList: List<Course>,
    private val onDeleteClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseIconImageView: ImageView = view.findViewById(R.id.courseIconImageView)
        val courseCodeTextView: TextView = view.findViewById(R.id.courseCodeTextView)
        val courseNameTextView: TextView = view.findViewById(R.id.courseNameTextView)
        val departmentTextView: TextView = view.findViewById(R.id.departmentTextView)
        val deleteButton: Button = view.findViewById(R.id.deletebtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]


        holder.courseCodeTextView.text = course.courseCode
        holder.courseNameTextView.text = course.courseName
        holder.departmentTextView.text = course.dept

        Glide.with(holder.courseIconImageView.context)
            .load(course.icon)
            .placeholder(R.drawable.ic_placeholder)

            .into(holder.courseIconImageView)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(course)
        }
    }

    override fun getItemCount(): Int = courseList.size
}
