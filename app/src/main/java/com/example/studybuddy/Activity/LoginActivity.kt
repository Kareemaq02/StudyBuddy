package com.example.studybuddy.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studybuddy.R
import com.example.studybuddy.auth.UserAuth
import com.example.studybuddy.data.Session
import com.google.firebase.database.*
class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = "example@example.com"
            val password = "password123"

            // Validate credentials
            if (validateCredentials(email, password)) {
                // Perform login
                login(email, password)
            } else {
                // Show error message for invalid credentials
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        // Perform any necessary validation on the email and password
        // For example, check if the email is in the correct format
        // You can use regular expressions or other validation methods
        // Return true if the credentials are valid, false otherwise
        // You can customize this method based on your specific validation requirements
        return email.isNotEmpty() && password.isNotEmpty()
    }

    private fun login(email: String, password: String) {
        // Call the UserAuth class to perform the login process
        UserAuth.login(email, password) { success ->
            if (success) {
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val collectionRef: DatabaseReference = database.getReference("Sessions")
                val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                val query = collectionRef.orderByChild("deviceId").equalTo(deviceId)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val isDeviceRegistered = dataSnapshot.exists()

                        if (!isDeviceRegistered) {
                            // The device ID is not registered, create a new session
                            val key = collectionRef.push().key
                            val session = Session(email, deviceId)

                            if (key != null) {
                                collectionRef.child(key).setValue(session)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@LoginActivity, "Session created successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { error ->
                                        println("Failed to add session: $error")
                                    }
                            } else {
                                Toast.makeText(this@LoginActivity, "Login failed, please try again later", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // Navigate to the home screen or main app screen
                        val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the database error, if necessary
                        // For example, display an error message or log the error
                    }
                })
            } else {
                // Login failed, display an error message or handle the case
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
