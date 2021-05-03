package com.example.brannvarsling.extensions

import android.app.Activity
import android.widget.Toast

object Extensions {
    fun Activity.toast(msg: String, lengthLong: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}