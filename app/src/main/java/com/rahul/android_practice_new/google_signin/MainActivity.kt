package com.rahul.android_practice_new.google_signin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.rahul.android_practice_new.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInButton: Button
    private lateinit var userDisplayName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userProfileImage: CircleImageView
    private lateinit var userStatus: TextView

    private val scope = CoroutineScope(Dispatchers.Main)

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            doGoogleSignIn()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupClickListeners()

    }

    private fun initializeViews() {
        googleSignInButton = findViewById(R.id.google_sign_in_button)
        userDisplayName = findViewById(R.id.user_display_name)
        userEmail = findViewById(R.id.user_email)
        userProfileImage = findViewById(R.id.user_profile_image)
        userStatus = findViewById(R.id.user_status)

        hideUserDetails()
    }

    private fun setupClickListeners() {
        googleSignInButton.setOnClickListener {
            doGoogleSignIn()
        }
    }

    private fun doGoogleSignIn() {
        GoogleSignInUtils.doGoogleSignIn(
            context = this,
            scope = scope,
            launcher = launcher,
            onLoginSuccess = { googleUserDetails ->
                // Call your API with Google user details
                Log.d("check_login","login details -> ${googleUserDetails}")
                showUserDetails(googleUserDetails)
            },
            onLoginFailure = { exception ->
                runOnUiThread {
                    Toast.makeText(this, "Google login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showUserDetails(googleUserDetails: GoogleUserDetails) {
        userDisplayName.text = googleUserDetails.displayName.ifEmpty { "No name provided" }
        userEmail.text = googleUserDetails.email.ifEmpty { "No email provided" }
        userStatus.text = if (googleUserDetails.isEmailVerified) "Email Verified ✓" else "Email Not Verified"

        // Load profile image
        if (googleUserDetails.profilePictureUrl.isNotEmpty()) {
            Glide.with(this)
                .load(googleUserDetails.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(userProfileImage)
        }

        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.user_details_container).visibility = android.view.View.VISIBLE
        googleSignInButton.visibility = android.view.View.GONE
    }

    private fun hideUserDetails() {
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.user_details_container).visibility = android.view.View.GONE
        googleSignInButton.visibility = android.view.View.VISIBLE
    }

    private fun saveAuthData(loginResponse: LoginResponse) {
        // Save your JWT token, user data to SharedPreferences or secure storage
        val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("authToken", loginResponse.token)
        editor.putString("userId", loginResponse.user.id)
        editor.putString("userEmail", loginResponse.user.email)
        editor.apply()

        // You can also update your API service with the new token
    }

    fun onSignOutClick(view: android.view.View) {
        // Clear auth data and reset UI
        val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        hideUserDetails()
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
    }
}

data class LoginResponse(
    val token: String,
    val user: ApiUser
)

data class ApiUser(
    val id: String,
    val email: String,
    val name: String
)