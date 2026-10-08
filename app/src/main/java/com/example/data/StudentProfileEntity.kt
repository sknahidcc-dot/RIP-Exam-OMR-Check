package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey val rollNumber: String,
    val name: String,
    val batch: String = "RIP Batch",
    val createdTimestamp: Long = System.currentTimeMillis()
)
