package uz.ollohberdi.utils

import android.content.Context
import android.content.SharedPreferences

object LanguageShared {
    private const val NAME = "KeshXotiraFayli"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context?) {
        preferences = context!!.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var language: Int
        get() = preferences.getInt("animation", 0)
        set(value) = preferences.edit {
            it.putInt("animation", value)
        }
}