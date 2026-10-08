package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Student Profiles
    @Query("SELECT * FROM student_profiles ORDER BY rollNumber ASC")
    fun getAllStudentProfiles(): Flow<List<StudentProfileEntity>>

    @Query("SELECT * FROM student_profiles WHERE rollNumber = :roll LIMIT 1")
    suspend fun getStudentProfile(roll: String): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(student: StudentProfileEntity)

    @Delete
    suspend fun deleteStudentProfile(student: StudentProfileEntity)

    // Exam Attempts
    @Query("SELECT * FROM exam_attempts ORDER BY timestamp DESC")
    fun getAllExamAttempts(): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts WHERE studentRoll = :roll ORDER BY timestamp ASC")
    fun getAttemptsForStudent(roll: String): Flow<List<ExamAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamAttempt(attempt: ExamAttemptEntity): Long

    @Delete
    suspend fun deleteExamAttempt(attempt: ExamAttemptEntity)

    @Query("DELETE FROM exam_attempts")
    suspend fun clearAllAttempts()
}
