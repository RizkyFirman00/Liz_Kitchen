package com.dissy.lizkitchen.repository

import android.util.Log
import android.widget.Toast
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await


class UserRepository {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    suspend fun registerUser(email: String, phoneNumber: String, username: String, password: String): Boolean {
        return try {
            val existingUser = usersCollection.document(username).get().await()
            val userId = usersCollection.document().id
            if (existingUser.exists()) {
                false
            } else {
                val userData = hashMapOf(
                    "userId" to userId,
                    "email" to email,
                    "phoneNumber" to phoneNumber,
                    "username" to username,
                    "password" to password
                )
                usersCollection.document(username).set(userData).await()
                true
            }
        } catch (e: Exception) {
            Log.d("Register", "$e")
            false
        }
    }

    suspend fun loginUser(username: String, password: String, coroutineScope: CoroutineScope): Pair<Boolean, String?> {
        return try {
            val userDoc = usersCollection.document(username).get().await()

            if (userDoc.exists()) {
                val storedPassword = userDoc.getString("password")

                if (storedPassword == password) {
                    val userId = userDoc.getString("userId")
                    Pair(true, userId)
                } else {
                    Pair(false, null)
                }
            } else {
                Pair(false, null)
            }
        } catch (e: Exception) {
            Pair(false, null)
        }
    }

    suspend fun getUserData(userId: String): DocumentSnapshot? {
        return try {
            usersCollection.document(userId).get().await()
        } catch (e: Exception) {
            null
        }
    }

}