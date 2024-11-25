package com.example.scholaadmin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddDepartmentActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var departmentCode: EditText
    private lateinit var departmentName: EditText
    private lateinit var departmentShortName: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_department)


        database = FirebaseDatabase.getInstance().getReference("Department")


        departmentCode = findViewById(R.id.departmentCode)
        departmentName = findViewById(R.id.departmentName)
        departmentShortName = findViewById(R.id.departmentShortName)
        submitButton = findViewById(R.id.submitButton)

        val backIcon_dept = findViewById<ImageView>(R.id.backIcon_dept)
        backIcon_dept.setOnClickListener {
            finish()
        }


        submitButton.setOnClickListener {
            val code = departmentCode.text.toString()
            val name = departmentName.text.toString()
            val shortName = departmentShortName.text.toString()

            if (code.isNotEmpty() && name.isNotEmpty() && shortName.isNotEmpty()) {

                val department = Dept(dept_code= code, dept_name = name, dept_short_name = shortName)

                database.child(code).setValue(department).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Department added", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to add department", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
