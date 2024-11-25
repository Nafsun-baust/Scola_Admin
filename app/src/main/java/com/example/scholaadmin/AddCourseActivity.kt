package com.example.scholaadmin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddCourseActivity : AppCompatActivity() {

    private lateinit var courseCodeEditText: EditText
    private lateinit var courseNameEditText: EditText
    private lateinit var departmentSpinner: Spinner
    private lateinit var courseTypeSpinner: Spinner
    private lateinit var uploadIcon: ImageView
    private lateinit var submitButton: Button

    private var iconUri: Uri? = null
    private val database = FirebaseDatabase.getInstance().getReference("Course")
    private val storage = FirebaseStorage.getInstance().getReference("CourseIcons")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        courseCodeEditText = findViewById(R.id.courseCodeEditText)
        courseNameEditText = findViewById(R.id.courseNameEditText)
        departmentSpinner = findViewById(R.id.departmentSpinner)
        courseTypeSpinner = findViewById(R.id.course_type)
        uploadIcon = findViewById(R.id.imageView2)
        submitButton = findViewById(R.id.submitButton)

        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish()
        }

        val departments = arrayOf("CSE", "EEE", "ME")
        val departmentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        departmentSpinner.adapter = departmentAdapter

        val courseTypes = arrayOf("Theory", "Lab")
        val courseTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseTypes)
        courseTypeSpinner.adapter = courseTypeAdapter

        uploadIcon.setOnClickListener {
            openImagePicker()
        }

        submitButton.setOnClickListener {
            saveCourseData()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            iconUri = data?.data
            Toast.makeText(this, "Icon selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCourseData() {
        val courseCode = courseCodeEditText.text.toString()
        val courseName = courseNameEditText.text.toString()
        val department = departmentSpinner.selectedItem.toString()
        val courseType = courseTypeSpinner.selectedItem.toString()

        if (courseCode.isEmpty() || courseName.isEmpty() || iconUri == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val iconRef = storage.child("$courseCode.jpg")
        iconUri?.let {
            iconRef.putFile(it).addOnSuccessListener {
                iconRef.downloadUrl.addOnSuccessListener { uri ->
                    val course = Course(courseCode, courseName, department, uri.toString(), courseType)

                    database.child(courseCode).setValue(course).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload icon", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
