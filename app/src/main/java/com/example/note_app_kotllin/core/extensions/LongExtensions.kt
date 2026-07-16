package com.example.note_app_kotllin.core.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

 fun Long.toTimeLabel(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this))