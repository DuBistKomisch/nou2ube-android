package es.jakebarn.nou2ube.util

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

/*
 * Taken from official tutorials and sample code:
 *
 * https://github.com/googlesamples/android-databinding/blob/
 * 347dd33538b2e74e2713524d3a96ee12a4cd1575/BasicSample/app/src/main/java/com/
 * example/android/databinding/basicsample/util/ObservableViewModel.kt
 */
open class ObservableViewModel : ViewModel(), Observable {
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}