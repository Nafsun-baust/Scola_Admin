package com.example.scholaadmin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class TeacherListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var teacherRecyclerView: RecyclerView
    private lateinit var teacherAdapter: TeacherAdapter
    private lateinit var addTeacherButton: Button
    private lateinit var searchView: SearchView
    private val teacherList = mutableListOf<Teacher>()
    private val filteredList = mutableListOf<Teacher>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_list)

        try {
            database = FirebaseDatabase.getInstance().getReference("Teacher")

            teacherRecyclerView = findViewById(R.id.teachersRecyclerView)
            teacherRecyclerView.layoutManager = GridLayoutManager(this, 2)
            teacherAdapter = TeacherAdapter(filteredList, onDeleteClick = { teacher ->
                confirmAndDeleteTeacher(teacher)
            })
            teacherRecyclerView.adapter = teacherAdapter

            searchView = findViewById(R.id.searchField)
            setupSearchView()

            addTeacherButton = findViewById(R.id.addTeacherButton)
            addTeacherButton.setOnClickListener {
                val intent = Intent(this, AddTeacherActivity::class.java)
                startActivity(intent)
            }
            val backIcon = findViewById<ImageView>(R.id.backIcon)
            backIcon.setOnClickListener {
                finish()
            }

            loadTeachers()
        } catch (e: Exception) {
            Log.e("TeacherListActivity", "Error during initialization: ${e.message}")
        }
    }

    private fun loadTeachers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                teacherList.clear()
                for (teacherSnapshot in snapshot.children) {
                    val teacher = teacherSnapshot.getValue(Teacher::class.java)
                    if (teacher != null) {
                        teacherList.add(teacher)
                    } else {
                        Log.e("TeacherListActivity", "Null teacher object found in snapshot: ${teacherSnapshot.key}")
                    }
                }
                filterTeachers(searchView.query.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TeacherListActivity", "Firebase error: ${error.message}")
            }
        })
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTeachers(newText)
                return true
            }
        })
    }

    private fun filterTeachers(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(teacherList)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredList.addAll(teacherList.filter {
                it.name?.lowercase()?.contains(lowerCaseQuery) == true
            })
        }
        teacherAdapter.notifyDataSetChanged()
    }

    private fun confirmAndDeleteTeacher(teacher: Teacher) {
        AlertDialog.Builder(this)
            .setTitle("Delete Teacher")
            .setMessage("Are you sure you want to delete ${teacher.name}?")
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                deleteTeacher(teacher)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteTeacher(teacher: Teacher) {
        teacher.id?.let { teacherId ->
            database.child(teacherId).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    teacherList.remove(teacher)
                    filteredList.remove(teacher)
                    teacherAdapter.notifyDataSetChanged()
                    Log.i("TeacherListActivity", "Teacher deleted successfully: $teacherId")
                } else {
                    Log.e("TeacherListActivity", "Failed to delete teacher: $teacherId")
                }
            }
        } ?: Log.e("TeacherListActivity", "Invalid teacher ID")
    }
}
