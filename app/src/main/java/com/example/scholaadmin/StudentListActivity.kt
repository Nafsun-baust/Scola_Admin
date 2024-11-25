package com.example.scholaadmin

import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class StudentListActivity : AppCompatActivity() {

    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var searchView: SearchView
    private lateinit var studentDatabase: DatabaseReference
    private lateinit var departmentDatabase: DatabaseReference
    private val studentList = mutableListOf<Student>() // List to hold all student data
    private val filteredList = mutableListOf<Student>() // List for search filtering
    private val departmentShortNameMap = mutableMapOf<String, String>() // Map of dept_code -> dept_short_name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list) // Ensure this matches your layout file

        studentsRecyclerView = findViewById(R.id.studentsRecyclerView)
        searchView = findViewById(R.id.searchField) // SearchView in your layout

        // Initialize Firebase database references
        studentDatabase = FirebaseDatabase.getInstance().getReference("Student")
        departmentDatabase = FirebaseDatabase.getInstance().getReference("Department")

        // Set up the RecyclerView with a GridLayoutManager
        studentsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        studentAdapter = StudentAdapter(filteredList)
        studentsRecyclerView.adapter = studentAdapter

        // First, load department short names
        loadDepartmentShortNames()
        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish() // Closes the current activity and returns to the previous one
        }

        // Set up SearchView
        setupSearchView()
    }

    private fun loadDepartmentShortNames() {
        departmentDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (departmentSnapshot in snapshot.children) {
                    val deptCode = departmentSnapshot.key
                    val deptShortName = departmentSnapshot.child("dept_short_name").getValue(String::class.java)
                    if (deptCode != null && deptShortName != null) {
                        departmentShortNameMap[deptCode] = deptShortName
                    }
                }
                loadStudentsFromFirebase()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@StudentListActivity,
                    "Failed to load departments: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadStudentsFromFirebase() {
        studentDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear() // Clear the list to avoid duplicate entries
                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    if (student != null) {
                        // Replace dept_code with dept_short_name using the departmentShortNameMap
                        student.dept = departmentShortNameMap[student.dept] ?: "Unknown Department"
                        studentList.add(student)
                    }
                }
                // Initially, show all students
                filteredList.clear()
                filteredList.addAll(studentList)
                studentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@StudentListActivity,
                    "Failed to load students: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // No action on submit
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterStudents(newText)
                return true
            }
        })
    }

    private fun filterStudents(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            // Show all students if search query is empty
            filteredList.addAll(studentList)
        } else {
            val searchText = query.lowercase()
            // Filter students based on name, ID, or department short name
            filteredList.addAll(
                studentList.filter {
                    it.name?.lowercase()?.contains(searchText) == true ||
                            it.id?.lowercase()?.contains(searchText) == true ||
                            it.dept?.lowercase()?.contains(searchText) == true
                }
            )
        }
        studentAdapter.notifyDataSetChanged()
    }
}
