package com.example.scholaadmin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CourseListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var coursesRecyclerView: RecyclerView
    private val courses = mutableListOf<Course>()
    private val filteredCourses = mutableListOf<Course>()
    private lateinit var adapter: CourseAdapter
    private lateinit var searchField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        database = FirebaseDatabase.getInstance().getReference("Course")

        coursesRecyclerView = findViewById(R.id.coursesRecyclerView)
        adapter = CourseAdapter(filteredCourses) { course -> confirmAndDeleteCourse(course) }
        coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        coursesRecyclerView.adapter = adapter

        loadCourses()

        val addCourseButton = findViewById<Button>(R.id.addCourseButton)
        addCourseButton.setOnClickListener {
            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)
        }

        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish()
        }
        searchField = findViewById(R.id.searchField)
        setupSearchFunctionality()
    }

    private fun loadCourses() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                courses.clear()
                for (courseSnapshot in snapshot.children) {
                    val course = courseSnapshot.getValue(Course::class.java)
                    if (course != null) {
                        courses.add(course)
                    }
                }
                filteredCourses.clear()
                filteredCourses.addAll(courses)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CourseListActivity, "Failed to load courses.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchFunctionality() {
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterCourses(s.toString())
            }
        })
    }

    private fun filterCourses(query: String) {
        filteredCourses.clear()
        if (query.isEmpty()) {
            filteredCourses.addAll(courses)
        } else {
            val searchText = query.lowercase()
            for (course in courses) {
                val name = course.courseName?.lowercase() ?: ""
                val code = course.courseCode.lowercase()
                val dept = course.dept.lowercase()
                if (name.contains(searchText) || code.contains(searchText) || dept.contains(searchText)) {
                    filteredCourses.add(course)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun confirmAndDeleteCourse(course: Course) {
        // Show a confirmation dialog before deleting
        AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete the course: ${course.courseName}?")
            .setPositiveButton("Yes") { _, _ ->
                deleteCourse(course)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteCourse(course: Course) {
        course.courseCode?.let { code ->
            database.child(code).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Course deleted successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to delete course.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: Toast.makeText(this, "Invalid course code.", Toast.LENGTH_SHORT).show()
    }
}
