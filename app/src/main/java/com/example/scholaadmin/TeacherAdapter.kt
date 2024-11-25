package com.example.scholaadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TeacherAdapter(
    private val teacherList: List<Teacher>,
    private val onDeleteClick: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    class TeacherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.teacherName)
        val des: TextView = view.findViewById(R.id.teacherPosition)
        val profilePic: ImageView = view.findViewById(R.id.teacherImage)
        val deleteButton: ImageView = view.findViewById(R.id.delete_teacher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.teacher_item, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teacherList[position]
        holder.name.text = teacher.name
        holder.des.text = teacher.des
        Glide.with(holder.profilePic.context).load(teacher.profilePic).into(holder.profilePic)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(teacher)
        }
    }

    override fun getItemCount(): Int = teacherList.size
}
