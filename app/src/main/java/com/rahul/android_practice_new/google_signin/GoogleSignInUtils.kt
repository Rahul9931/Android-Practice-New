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
            onLoginSuccess: (GoogleUserDetails) -> Unit,
            onLoginFailure: (Exception?) -> Unit = {}
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

                                // Extract user details from Google credentials
                                val userDetails = GoogleUserDetails(
                                    idToken = googleIdTokenCredential.idToken,
                                    displayName = googleIdTokenCredential.displayName ?: "",
                                    email = googleIdTokenCredential.id,
                                    givenName = googleIdTokenCredential.givenName ?: "",
                                    familyName = googleIdTokenCredential.familyName ?: "",
                                    profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: "",
                                )

                                // Log user details
                                logUserDetails(userDetails)

                                // Pass user details to callback
                                onLoginSuccess(userDetails)
                            }
                        }
                        else ->{
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

        private fun logUserDetails(userDetails: GoogleUserDetails) {
            Log.d("GoogleSignIn", "=== GOOGLE USER DETAILS ===")
            Log.d("GoogleSignIn", "ID Token: ${userDetails.idToken}")
            Log.d("GoogleSignIn", "Display Name: ${userDetails.displayName}")
            Log.d("GoogleSignIn", "Email: ${userDetails.email}")
            Log.d("GoogleSignIn", "Given Name: ${userDetails.givenName}")
            Log.d("GoogleSignIn", "Family Name: ${userDetails.familyName}")
            Log.d("GoogleSignIn", "Profile Picture: ${userDetails.profilePictureUrl}")
            Log.d("GoogleSignIn", "Email Verified: ${userDetails.isEmailVerified}")
            Log.d("GoogleSignIn", "=========================")
        }
    }
}

// Data class to hold Google user details
data class GoogleUserDetails(
    val idToken: String,
    val displayName: String,
    val email: String,
    val givenName: String,
    val familyName: String,
    val profilePictureUrl: String,
    val isEmailVerified: Boolean = false
)