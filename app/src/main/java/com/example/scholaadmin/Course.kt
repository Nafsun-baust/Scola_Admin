package com.example.scholaadmin

data class Course(
    val courseCode: String = "",
    val courseName: String = "",
    val dept: String = "",
    val icon: String? = null,
    var type: String?=null
)
