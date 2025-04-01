package com.android.onboardingscreen.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.android.onboardingscreen.R

class AuthenticationManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun isUserSignedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    fun signOut() {
        auth.signOut()
        // Clear any stored preferences
        sharedPreferences.edit().clear().apply()
    }

    // Optional: Store additional user data
    fun storeUserData(userId: String, email: String) {
        sharedPreferences.edit().apply {
            putString("user_id", userId)
            putString("email", email)
            apply()
        }
    }

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Error(task.exception ?: Exception("Unknown error")))
                }
            }
        awaitClose()
    }

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Error(task.exception ?: Exception("Unknown error")))
                }
            }
        awaitClose()
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setAutoSelectEnabled(false)
                .setNonce(createNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential && 
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken, null
                    )

                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(AuthResponse.Success)
                            } else {
                                trySend(AuthResponse.Error(
                                    task.exception ?: Exception("Unknown error")
                                ))
                            }
                        }
                } else {
                    trySend(AuthResponse.Error(Exception("Invalid credential type")))
                }
            } catch (e: Exception) {
                trySend(AuthResponse.Error(e))
            }
        } catch (e: Exception) {
            trySend(AuthResponse.Error(e))
        }
        awaitClose()
    }
}
