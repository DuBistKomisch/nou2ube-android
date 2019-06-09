package es.jakebarn.nou2ube

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import es.jakebarn.nou2ube.data.BackendService
import es.jakebarn.nou2ube.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var tag = "MainActivity"
    private var rcSignIn = 1

    private lateinit var session: Session

    private lateinit var googleSignInClient: GoogleSignInClient

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

        backendService = BackendService(this)

        val signInButton: SignInButton = findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, rcSignIn)
        }

        val signOutButton: Button = findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                session.signOut()
            }
        }

        if (session.signedIn) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = backendService.restore()
                withContext(Dispatchers.Main) {
                    session.signIn(user.id, user.email, user.authenticationToken)
                }

                val subscriptions = backendService.getSubscriptions()
                val firstSubscription = subscriptions.first()
                val firstChannel = firstSubscription.channel.get(firstSubscription.document)
                Log.i(tag, "subscriptions: ${subscriptions.size}, first channel: ${firstChannel.title}")

                val items = backendService.getItems()
                val firstItem = items.first()
                val firstVideo = firstItem.video.get(firstItem.document)
                Log.i(tag, "items: ${items.size}, first video: ${firstVideo.title} ${firstVideo.publishedAt}")
            }
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
        completedTask.result?.serverAuthCode?.let { code ->
            CoroutineScope(Dispatchers.IO).launch {
                val user = backendService.signIn(code)
                withContext(Dispatchers.Main) {
                    session.signIn(user.id, user.email, user.authenticationToken)
                }
            }
        }
    }
}
