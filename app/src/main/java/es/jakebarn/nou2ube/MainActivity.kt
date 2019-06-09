package es.jakebarn.nou2ube

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.squareup.moshi.Moshi
import es.jakebarn.nou2ube.databinding.ActivityMainBinding
import moe.banana.jsonapi2.JsonApiConverterFactory
import moe.banana.jsonapi2.ResourceAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private var tag = "MainActivity"
    private var rcSignIn = 1

    private lateinit var session: Session

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var backendClient: OkHttpClient
    private lateinit var retrofit: Retrofit
    private lateinit var backendService: BackendService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        session = Session.getInstance(this)
        binding.session = session

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.EMAIL), Scope(getString(R.string.scope_youtube_readonly)))
            .requestServerAuthCode(getString(R.string.server_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        backendClient = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        val factory = ResourceAdapterFactory.builder()
            .add(User::class.java)
            .build()
        val moshi = Moshi.Builder()
            .add(factory)
            .build()

        retrofit = Retrofit.Builder()
            .client(backendClient)
            .baseUrl(getString(R.string.server_origin))
            .addConverterFactory(JsonApiConverterFactory.create(moshi))
            .build()
        backendService = retrofit.create(BackendService::class.java)

        val signInButton: SignInButton = findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, rcSignIn)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        val account = completedTask.result
        val code = account?.serverAuthCode
        Log.d(tag, "got code: $code")
        if (code != null) {
            backendService.signIn(code).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val user = response.body()
                    if (user != null) {
                        session.signIn(user.id, user.email, user.authenticationToken)
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(tag, "sign in request failed", t)
                }
            })
        }
    }
}
