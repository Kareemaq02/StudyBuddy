package com.example.studybuddy.UserFragments

import GlobalData
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.studybuddy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class UserSettingsFragment : Fragment() {

    private lateinit var changePasswordButton: Button
    private lateinit var newPasswordEditText: EditText

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var currentUserRef: DatabaseReference
    private lateinit var userFirstName: TextView
    private lateinit var userLastName: TextView
    private lateinit var userEmail: TextView
    private lateinit var email: String
    private lateinit var showFirstName: String
    private lateinit var showLastName: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_settings, container, false)

        userFirstName = view.findViewById(R.id.firstname)
        //userLastName = view.findViewById(R.id.lastname)
        userEmail = view.findViewById(R.id.email)
        changePasswordButton = view.findViewById(R.id.button4)
        //newPasswordEditText = view.findViewById(R.id.edit_text_new_password)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.reference.child("users")

        // Get the current user reference using the email
        email = GlobalData.userEmail
        showFirstName = GlobalData.userFirstName
        showLastName = GlobalData.userLastName

        currentUserRef = usersRef.orderByChild("email").equalTo(email).limitToFirst(1).ref

        displayUserInfo()

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        return view
    }

    private fun displayUserInfo() {

        val userQuery = usersRef.orderByChild("email").equalTo(email)
        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val firstName = userSnapshot.child("firstname").getValue(String::class.java)

                    Log.d("UserSettingsFragment", "First Name: $firstName")

                    userFirstName.text = "Welcome $firstName"
                    userEmail.text = "Email: $email"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")
        builder.setMessage("Enter your new password")

        val passwordEditText = EditText(requireContext())
        builder.setView(passwordEditText)

        builder.setPositiveButton("Change") { dialogInterface: DialogInterface, _: Int ->
            val newPassword = passwordEditText.text.toString().trim()

            if (newPassword.isNotEmpty()) {
                // Update the password in Firebase
                editUserPassword(newPassword)
            } else {
                // Show an error message for empty password
                showInvalidPasswordDialog()
            }

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun editUserPassword(newPassword: String) {
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {

                        // Update the password value for the user
                        userSnapshot.child("password").ref.setValue(newPassword)
                            .addOnSuccessListener {
                                // Password update successful
                                showPasswordChangeSuccessDialog()
                            }
                            .addOnFailureListener { exception ->
                                // Password update failed
                                // Handle the error here
                                showPasswordChangeFailureDialog()
                            }
                    }
                } else {
                    // User not found
                    showUserNotFoundErrorDialog()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the database error here
                showDatabaseErrorDialog()
            }
        })
    }

    private fun showPasswordChangeSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Password Change Success")
        builder.setMessage("Your password has been changed successfully.")

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showPasswordChangeFailureDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Password Change Failed")
        builder.setMessage("Failed to change your password. Please try again.")

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showInvalidPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Invalid Password")
        builder.setMessage("Please enter a valid password.")

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showUserNotFoundErrorDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("User Not Found")
        builder.setMessage("Failed to find the user. Please try again.")

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDatabaseErrorDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Database Error")
        builder.setMessage("An error occurred while accessing the database. Please try again.")

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
