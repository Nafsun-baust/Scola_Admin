package com.example.scholaadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var departmentDatabase: DatabaseReference
    private lateinit var courseDatabase: DatabaseReference
    private lateinit var teacherDatabase: DatabaseReference
    private lateinit var studentDatabase: DatabaseReference
    private lateinit var departmentCountTextView: TextView
    private lateinit var teacherCountTextView: TextView
    private lateinit var courseCountTextView: TextView
    private lateinit var studentCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
                          //show

        departmentDatabase = FirebaseDatabase.getInstance().getReference("Department")
        courseDatabase = FirebaseDatabase.getInstance().getReference("Course")
        teacherDatabase = FirebaseDatabase.getInstance().getReference("Teacher")
        studentDatabase = FirebaseDatabase.getInstance().getReference("Student")


        val button_department = findViewById<Button>(R.id.button_department)
        val button_teachers = findViewById<Button>(R.id.button_teachers)
        val button_students = findViewById<Button>(R.id.button_students)
        val courses_button = findViewById<Button>(R.id.courses_button)
        departmentCountTextView = findViewById(R.id.department_no)
        courseCountTextView = findViewById(R.id.courses_no)
        teacherCountTextView = findViewById(R.id.teacher_no)
        studentCountTextView = findViewById(R.id.student_no)

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        button_department.setOnClickListener {
            val intent = Intent(this, DepartmentListActivity::class.java)
            startActivity(intent)
        }

        button_teachers.setOnClickListener {
            val intent = Intent(this, TeacherListActivity::class.java)
            startActivity(intent)
        }

        button_students.setOnClickListener {
            val intent = Intent(this, StudentListActivity::class.java)
            startActivity(intent)
        }

        courses_button.setOnClickListener {
            val intent = Intent(this, CourseListActivity::class.java)
            startActivity(intent)
        }
//     realtime database load korci
        loadDepartmentCount()
        loadCourseCount()
        loadTeacherCount()
        loadstudentCount()
    }

    private fun loadDepartmentCount() {
        departmentDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                departmentCountTextView.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun loadTeacherCount() {
        teacherDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                teacherCountTextView.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun loadstudentCount() {
        studentDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                studentCountTextView.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadCourseCount() {
        courseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                courseCountTextView.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
