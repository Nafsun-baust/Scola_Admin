package com.example.scholaadmin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddTeacherActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var teacherIdEditText: EditText
    private lateinit var designationEditText: EditText
    private lateinit var qualificationEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var departmentSpinner: Spinner
    private lateinit var advisorCheckBox: CheckBox
    private lateinit var submitButton: Button
    private lateinit var profilePicImageView: ImageView

    private var profilePicUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val database = FirebaseDatabase.getInstance().getReference("Teacher")
    private val storage = FirebaseStorage.getInstance().getReference("TeacherPictures")

    private var selectedDepartmentCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_teacher)

        nameEditText = findViewById(R.id.firstName)
        teacherIdEditText = findViewById(R.id.teacherId)
        designationEditText = findViewById(R.id.designation)
        qualificationEditText = findViewById(R.id.qualification)
        emailEditText = findViewById(R.id.email)
        phoneEditText = findViewById(R.id.phone)
        passwordEditText = findViewById(R.id.password)
        departmentSpinner = findViewById(R.id.departmentSpinner)
        advisorCheckBox = findViewById(R.id.advisorCheckBox)
        submitButton = findViewById(R.id.submitButton)
        profilePicImageView = findViewById(R.id.Teacher_picture)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                profilePicUri = result.data?.data
                profilePicImageView.setImageURI(profilePicUri)
            }
        }

        populateDepartmentSpinner()

        profilePicImageView.setOnClickListener {
            openImagePicker()
        }

        submitButton.setOnClickListener {
            saveTeacherData()
        }
        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish()
        }
    }

    private fun populateDepartmentSpinner() {
        val departmentMap = mutableMapOf<String, String>()
        val databaseReference = FirebaseDatabase.getInstance().getReference("Department")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val departmentNames = mutableListOf<String>()

                for (departmentSnapshot in snapshot.children) {
                    val departmentCode = departmentSnapshot.key
                    val departmentName = departmentSnapshot.child("dept_name").getValue(String::class.java)

                    if (departmentCode != null && departmentName != null) {
                        departmentMap[departmentName] = departmentCode
                        departmentNames.add(departmentName)
                    }
                }

                val adapter = ArrayAdapter(
                    this@AddTeacherActivity,
                    android.R.layout.simple_spinner_item,
                    departmentNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                departmentSpinner.adapter = adapter

                departmentSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedDepartmentName = parent?.getItemAtPosition(position).toString()
                        selectedDepartmentCode = departmentMap[selectedDepartmentName] ?: ""
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedDepartmentCode = ""
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddTeacherActivity, "Failed to load departments", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun saveTeacherData() {
        val name = nameEditText.text.toString()
        val teacherId = teacherIdEditText.text.toString()
        val designation = designationEditText.text.toString()
        val qualification = qualificationEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val password = passwordEditText.text.toString()
        val isAdvisor = advisorCheckBox.isChecked

        if (name.isEmpty() || teacherId.isEmpty() || designation.isEmpty() || qualification.isEmpty() ||
            email.isEmpty() || phone.isEmpty() || password.isEmpty() || profilePicUri == null || selectedDepartmentCode.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val profilePicRef = storage.child("$teacherId.jpg")
        profilePicRef.putFile(profilePicUri!!)
            .addOnSuccessListener {
                profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                    val teacherData = mapOf(
                        "id" to teacherId,
                        "name" to name,
                        "dept" to selectedDepartmentCode,
                        "des" to designation,
                        "ql" to qualification,
                        "advisor" to isAdvisor,
                        "email" to email,
                        "phone" to phone,
                        "password" to password,
                        "profilePic" to uri.toString()
                    )

                    database.child(teacherId).setValue(teacherData)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Teacher added successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to add teacher", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show()
            }
    }
}
