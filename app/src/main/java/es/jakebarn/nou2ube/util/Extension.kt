package es.jakebarn.nou2ube.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel

// https://stackoverflow.com/a/53510106/3453300
val AppCompatActivity.TAG: String get() = javaClass.simpleName
val ViewModel.TAG: String get() = javaClass.simpleName