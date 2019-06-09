package es.jakebarn.nou2ube

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.databinding.Bindable
import es.jakebarn.nou2ube.util.ObservableViewModel
import es.jakebarn.nou2ube.util.SingletonHolder

class Session private constructor(context: Context) : ObservableViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object : SingletonHolder<Session, Context>(::Session)

    private val tag = "Session"

    private val preferenceName = context.getString(R.string.session_preference_name)
    private val sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    @get:Bindable
    val id: String? get() = sharedPreferences.getString("id", null)
    @get:Bindable
    val email: String? get() = sharedPreferences.getString("email", null)
    @get:Bindable
    val authenticationToken: String? get() = sharedPreferences.getString("authenticationToken", null)
    @get:Bindable("id")
    val signedIn: Boolean get() = id != null

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == this.sharedPreferences) {
            val fieldId = when (key) {
                "id" -> BR.id
                "email" -> BR.email
                "authenticationToken" -> BR.authenticationToken
                else -> return
            }
            notifyPropertyChanged(fieldId)
        }
    }

    fun signIn(id: String, email: String, authenticationToken: String) {
        editor.putString("id", id)
        editor.putString("email", email)
        editor.putString("authenticationToken", authenticationToken)
        editor.apply()

        Log.i(tag, "signed in as ${this.email}")
    }

    fun signOut() {
        editor.remove("id")
        editor.remove("email")
        editor.remove("authenticationToken")
        editor.apply()

        Log.i(tag, "signed out")
    }
}