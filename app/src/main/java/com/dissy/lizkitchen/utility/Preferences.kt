package com.dissy.lizkitchen.utility

import android.content.Context
import android.content.SharedPreferences
import com.dissy.lizkitchen.model.User

object Preferences {

    fun init(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    private fun editor(context: Context, name: String): SharedPreferences.Editor {
        val sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sharedPref.edit()
    }

    fun saveUsername(username: String, context: Context){
        val editor = editor(context, "onSignIn")
        editor.putString("username", username)
        editor.apply()
    }

    fun checkUsername(context: Context): Boolean{
        val sharedPref = init(context, "onSignIn")
        val username = sharedPref.getString("username", null)
        return username != null
    }

    fun saveUserId(userId: String, context: Context){
        val editor = editor(context, "onSignIn")
        editor.putString("userId", userId)
        editor.apply()
    }

    fun getUserId(context: Context): String? {
        val sharedPref = init(context, "onSignIn")
        return sharedPref.getString("userId", null)
    }

    fun getUsername(context: Context): String? {
        val sharedPref = init(context, "onSignIn")
        return sharedPref.getString("username", null)
    }

    fun saveUserInfo(user: User, context: Context) {
        val editor = editor(context, "onSignIn")
        editor.putString("userId", user.userId)
        editor.putString("email", user.email)
        editor.putString("password", user.password)
        editor.putString("phoneNumber", user.phoneNumber)
        editor.putString("userId", user.userId)
        editor.putString("username", user.username)
        editor.putString("alamat", user.alamat)
        // Simpan properti lainnya sesuai kebutuhan
        editor.apply()
    }

    fun getUserInfo(context: Context): User? {
        val sharedPref = init(context, "onSignIn")
        return User(
            userId = sharedPref.getString("userId", null) ?: "",
            username = sharedPref.getString("username", null) ?: "",
            email = sharedPref.getString("email", null) ?: "",
            password = sharedPref.getString("password", null) ?: "",
            phoneNumber = sharedPref.getString("phoneNumber", null) ?: "",
            alamat = sharedPref.getString("alamat", null) ?: "Belum Diisi",
            // Ambil properti lainnya sesuai kebutuhan
        )
    }

    fun logout(context: Context){
        val editor = editor(context, "onSignIn")
        editor.remove("username")
        editor.remove("status")
        editor.remove("userId")
        editor.remove("email")
        editor.remove("password")
        editor.remove("phoneNumber")
        editor.remove("alamat")
        // Hapus properti lainnya sesuai kebutuhan
        editor.apply()
    }
}