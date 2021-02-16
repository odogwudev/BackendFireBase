package com.odogwudev.kotlinbackendfirebase.authentication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.odogwudev.kotlinbackendfirebase.R
import kotlinx.android.synthetic.main.activity_sign_in_with_google.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGNIN = 0

class SignInWithGoogleActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_sign_in_with_google)
        auth = FirebaseAuth.getInstance()

        btnSignIn.setOnClickListener {
            val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webcient_id))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(this, option)
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE_SIGNIN)

            }
        }
    }

    private fun googleAuthFireBase (account: GoogleSignInAccount){
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@SignInWithGoogleActivity, "Succesful SignIn", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@SignInWithGoogleActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== REQUEST_CODE_SIGNIN){
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthFireBase(it)
            }
        }
    }
}