package com.rahul.android_practice_new.google_signin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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

        // Hide user details initially
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
            onLoginSuccess = { userDetails ->
                runOnUiThread {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    showUserDetails(userDetails)
                }
            },
            onLoginFailure = { exception ->
                runOnUiThread {
                    Toast.makeText(this, "Login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    hideUserDetails()
                }
            }
        )
    }

    private fun showUserDetails(userDetails: UserDetails) {
        // Update UI with user details
        userDisplayName.text = userDetails.displayName.ifEmpty { "No name provided" }
        userEmail.text = userDetails.email.ifEmpty { "No email provided" }
        userStatus.text = if (userDetails.isEmailVerified) "Email Verified ✓" else "Email Not Verified"

        // Load profile image using Glide (add dependency: implementation 'com.github.bumptech.glide:glide:4.14.2')
        if (userDetails.photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(userDetails.photoUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(userProfileImage)
        }

        // Show user details section
        findViewById<ConstraintLayout>(R.id.user_details_container).visibility = View.VISIBLE
        googleSignInButton.visibility = View.GONE
    }

    private fun hideUserDetails() {
        findViewById<ConstraintLayout>(R.id.user_details_container).visibility = View.GONE
        googleSignInButton.visibility = View.VISIBLE
    }

    private fun signOut() {
        // Implement sign out functionality if needed
        Firebase.auth.signOut()
        hideUserDetails()
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
    }

}