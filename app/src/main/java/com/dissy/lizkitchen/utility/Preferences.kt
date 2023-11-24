package com.dissy.lizkitchen.utility

import android.content.Context
import android.content.SharedPreferences

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

    fun logout(context: Context){
        val editor = editor(context, "onSignIn")
        editor.remove("username")
        editor.remove("status")
        editor.remove("userId")
        editor.apply()
    }
}