package com.rahul.android_practice_new.google_signin

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.rahul.android_practice_new.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GoogleSignInUtils {

    companion object {
        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            launcher: ActivityResultLauncher<Intent>?,
            onLoginSuccess: (UserDetails) -> Unit, // Changed to pass user details
            onLoginFailure: (Exception?) -> Unit = {} // Optional failure callback
        ) {
            val credentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    when(result.credential){
                        is CustomCredential ->{
                            if(result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                val googleTokenId = googleIdTokenCredential.idToken
                                val authCredential = GoogleAuthProvider.getCredential(googleTokenId,null)

                                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                                val user = authResult.user

                                user?.let {
                                    if(it.isAnonymous.not()){
                                        // Extract user details
                                        val userDetails = UserDetails(
                                            uid = it.uid,
                                            displayName = it.displayName ?: "",
                                            email = it.email ?: "",
                                            photoUrl = it.photoUrl?.toString() ?: "",
                                            phoneNumber = it.phoneNumber ?: "",
                                            isEmailVerified = it.isEmailVerified,
                                            providerId = it.providerId,
                                            creationTimestamp = it.metadata?.creationTimestamp ?: 0,
                                            lastSignInTimestamp = it.metadata?.lastSignInTimestamp ?: 0
                                        )

                                        // Log user details
                                        logUserDetails(userDetails)

                                        // Pass user details to callback
                                        onLoginSuccess(userDetails)
                                    }
                                }
                            }
                        }
                        else ->{
                            // Handle other credential types if needed
                            onLoginFailure(Exception("Unsupported credential type"))
                        }
                    }
                } catch (e: NoCredentialException){
                    launcher?.launch(getIntent())
                } catch (e: GetCredentialException){
                    e.printStackTrace()
                    onLoginFailure(e)
                }
            }
        }

        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }

        private fun logUserDetails(userDetails: UserDetails) {
            Log.d("GoogleSignIn", "=== USER DETAILS ===")
            Log.d("GoogleSignIn", "UID: ${userDetails.uid}")
            Log.d("GoogleSignIn", "Display Name: ${userDetails.displayName}")
            Log.d("GoogleSignIn", "Email: ${userDetails.email}")
            Log.d("GoogleSignIn", "Email Verified: ${userDetails.isEmailVerified}")
            Log.d("GoogleSignIn", "Photo URL: ${userDetails.photoUrl}")
            Log.d("GoogleSignIn", "Phone Number: ${userDetails.phoneNumber}")
            Log.d("GoogleSignIn", "Provider ID: ${userDetails.providerId}")
            Log.d("GoogleSignIn", "Account Created: ${userDetails.creationTimestamp}")
            Log.d("GoogleSignIn", "Last Sign In: ${userDetails.lastSignInTimestamp}")
            Log.d("GoogleSignIn", "=========================")
        }
    }
}

// Data class to hold user details
data class UserDetails(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String,
    val phoneNumber: String,
    val isEmailVerified: Boolean,
    val providerId: String,
    val creationTimestamp: Long,
    val lastSignInTimestamp: Long
)