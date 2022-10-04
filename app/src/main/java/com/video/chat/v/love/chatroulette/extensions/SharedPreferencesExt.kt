package com.video.chat.v.love.chatroulette.extensions

import android.content.SharedPreferences


private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor: SharedPreferences.Editor = edit()
    operation(editor)
    editor.apply()
}

fun SharedPreferences.put(key: String, value: Any?) {
    when (value) {
        is Boolean -> edit { it.putBoolean(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Double -> edit { it.putLong(key, java.lang.Double.doubleToLongBits(value)) }
        is Long -> edit { it.putLong(key, value) }
        is String? -> edit { it.putString(key, value) }
    }
}

@Throws(UnsupportedOperationException::class)
inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? =
    when (T::class) {
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as? T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as? T?
        Float::class -> getFloat(key, defaultValue as Float? ?: -1f) as? T?
        Double::class -> java.lang.Double.longBitsToDouble(getLong(key, -1L)) as? T?
        Long::class -> getLong(key, defaultValue as Long? ?: -1L) as? T?
        String::class -> getString(key, defaultValue as String?) as? T?
        else -> throw UnsupportedOperationException("Class not supported by SharedPreferences.put()")
    }

fun SharedPreferences.remove(key: String) {
    edit().remove(key).apply()
}
