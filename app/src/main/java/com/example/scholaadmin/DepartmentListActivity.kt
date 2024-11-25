package com.example.scholaadmin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class DepartmentListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var departmentsRecyclerView: RecyclerView
    private lateinit var searchField: EditText
    private val departments = mutableListOf<Dept>()
    private lateinit var adapter: DepartmentAdapter
    private val filteredDepartments = mutableListOf<Dept>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department_list)

        database = FirebaseDatabase.getInstance().getReference("Department")

        departmentsRecyclerView = findViewById(R.id.departmentsRecyclerView)
        searchField = findViewById(R.id.searchField)
        adapter = DepartmentAdapter(filteredDepartments) { department ->
            deleteDepartment(department)
        }
        departmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        departmentsRecyclerView.adapter = adapter

        loadDepartments()

        val addDepartmentButton = findViewById<Button>(R.id.addDepartmentButton)
        addDepartmentButton.setOnClickListener {
            openAddDepartmentActivity()
        }

        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            finish()
        }

        setupSearchFunctionality()
    }

    private fun loadDepartments() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                departments.clear()
                for (departmentSnapshot in snapshot.children) {
                    val departmentCode = departmentSnapshot.key
                    val department = departmentSnapshot.getValue(Dept::class.java)
                    if (department != null && departmentCode != null) {
                        department.dept_code = departmentCode
                        departments.add(department)
                    }
                }
                filteredDepartments.clear()
                filteredDepartments.addAll(departments)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DepartmentListActivity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchFunctionality() {
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterDepartments(s.toString())
            }
        })
    }

    private fun filterDepartments(query: String) {
        filteredDepartments.clear()
        if (query.isEmpty()) {
            filteredDepartments.addAll(departments)
        } else {
            val searchText = query.toLowerCase()
            for (department in departments) {
                val name = department.dept_name?.toLowerCase() ?: ""
                val shortName = department.dept_short_name?.toLowerCase() ?: ""
                if (name.contains(searchText) || shortName.contains(searchText)) {
                    filteredDepartments.add(department)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun deleteDepartment(department: Dept) {
        department.dept_code?.let { code ->
            database.child(code).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    departments.remove(department)
                    filteredDepartments.remove(department)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Department deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to delete department", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: Toast.makeText(this, "Invalid department code", Toast.LENGTH_SHORT).show()
    }

    private fun openAddDepartmentActivity() {
        val intent = Intent(this, AddDepartmentActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
