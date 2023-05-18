// Inside SignupActivity.kt

package com.example.studybuddy.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studybuddy.R
import com.example.studybuddy.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val signupButton = findViewById<Button>(R.id.signupButton)
        signupButton.setOnClickListener {
            // Call the signup function and insert an example user
            signup()
        }


        // Code for signup functionality
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userData = User("example@example.com", "password123", "John", "Doe", "Computer Science")
        val newUserRef = usersRef.push()
        newUserRef.setValue(userData)

        // Other signup-related code, such as UI interactions and user input handling
        // ...
    }
    private fun signup() {
        val email = "example@example.com" // Replace with the actual email input
        val password = "password123" // Replace with the actual password input

        // Check if the email is already registered
        if(isValidEmail(email)) {
            isEmailAlreadyRegistered(email) { isRegistered ->
                if (isRegistered) {
                    // Email is already registered, display an error message or handle the case
                    // For example, show a Toast message
                    Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
                } else {
                    // Email is not registered, proceed with the signup process
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("users")
                    val userData = User(email, password, "John", "Doe", "Computer Science")
                    val newUserRef = usersRef.push()
                    newUserRef.setValue(userData)

                    // Show a success message or navigate to the next screen
                    // For example, show a Toast message and go back to the login screen
                    Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                    finish() // Finish the signup activity and go back to the login activity
                }
            }
        }
        //TODO Else
    }

    private fun isEmailAlreadyRegistered(email: String, callback: (Boolean) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val query = usersRef.orderByChild("email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // If a user with the given email exists, the email is already registered
                val isRegistered = dataSnapshot.exists()
                callback.invoke(isRegistered)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the database error, if necessary
                // For example, display an error message or log the error
                callback.invoke(false) // Assume email is not registered in case of an error
            }
        })
    }
    fun isValidEmail(email: String): Boolean {
        val pattern = Regex("^[A-Za-z0-9.]+@gju\\.edu\\.jo$")
        return pattern.matches(email)
    }


    // Other methods and functions related to signup functionality
    // ...
}
