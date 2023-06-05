package com.example.studybuddy.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.studybuddy.Activity.AdminAddCourseSettingActivity
import com.example.studybuddy.Activity.LoginActivity
import com.example.studybuddy.R
import com.google.firebase.database.*
import java.util.regex.Pattern

class SettingFragment : Fragment() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var changePasswordButton: Button
    private lateinit var pencilImageButton1: ImageButton
    private lateinit var pencilImageButton2: ImageButton
    private lateinit var logoutButton: Button
    private lateinit var addCourseButton: Button


    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        super.onViewCreated(view, savedInstanceState)


        firstNameEditText = view.findViewById(R.id.editTextTextPersonName4)
        lastNameEditText = view.findViewById(R.id.editTextTextPersonName3)
        changePasswordButton = view.findViewById(R.id.button4)
        pencilImageButton1 = view.findViewById(R.id.imageButton4)
        pencilImageButton2 = view.findViewById(R.id.imageButton3)
        logoutButton = view.findViewById(R.id.button2)
        addCourseButton = view.findViewById(R.id.button)
        emailTextView = view.findViewById(R.id.email)



        pencilImageButton1.setOnClickListener { confirmFirstNameChange() }
        pencilImageButton2.setOnClickListener { confirmLastNameChange() }
        changePasswordButton.setOnClickListener { showChangePasswordDialog() }
        logoutButton.setOnClickListener {showLogoutDialog()}
        addCourseButton.setOnClickListener {

            val intent = Intent(requireActivity(), AdminAddCourseSettingActivity::class.java)
            startActivity(intent)

        }
        val database = FirebaseDatabase.getInstance()
        val adminsRef = database.getReference("admin").child("11").child("email")
        adminsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val email = dataSnapshot.getValue(String::class.java)
                emailTextView.text = email


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("SettingFragment", "Failed to retrieve email: ${databaseError.message}")
            }
        })







        return view
    }

/*
    private fun showFirstNameFromFirebase(firstName: String) {
        firstNameEditText.setText(firstName)
    }

    private fun showLastNameFromFirebase(lastName: String) {
        lastNameEditText.setText(lastName)
    }

 */

    private fun confirmFirstNameChange() {
        val newFirstName = firstNameEditText.text.toString().trim()

        if (newFirstName.isNotEmpty()) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirm First Name Change")
            builder.setMessage("Are you sure you want to change your first name to \"$newFirstName\"?")

            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                val database = FirebaseDatabase.getInstance()
                val adminsRef = database.getReference("admin").child("11").child("firstname")
                adminsRef.setValue(newFirstName)

                dialogInterface.dismiss()
            }

            builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                // User denied the first name change
                // You can perform any actions here
                dialogInterface.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun confirmLastNameChange() {
        val newLastName = lastNameEditText.text.toString().trim()

        if (newLastName.isNotEmpty()) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirm Last Name Change")
            builder.setMessage("Are you sure you want to change your last name to \"$newLastName\"?")

            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                // User confirmed the last name change
                // You can perform any actions here, such as updating the value in Firebase
                val database = FirebaseDatabase.getInstance()
                val adminsRef = database.getReference("admin").child("11").child("lastname")
                adminsRef.setValue(newLastName)
                dialogInterface.dismiss()
            }

            builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                // User denied the last name change
                // You can perform any actions here
                dialogInterface.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")

        val oldPasswordEditText = EditText(requireContext())
        oldPasswordEditText.hint = "Enter old password"
        builder.setView(oldPasswordEditText)

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            val oldPassword = oldPasswordEditText.text.toString()
            //println("oldPass: $oldPassword")
            comparePasswordWithFirebase(oldPassword) { isMatch ->
                if (isMatch) {
                    // Passwords match
                    // Perform your desired action here, such as showing a password change form
                    showNewPasswordDialog()
                    println("Change Successfully")
                } else {
                    // Passwords do not match
                    // Perform your desired action here, such as showing an error message

                    showIncorrectPasswordDialog()

                    println("incorrect password")
                }
                dialogInterface.dismiss()
            }

        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun showNewPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")

        val newPasswordEditText = EditText(requireContext())
        newPasswordEditText.hint = "Enter new password"
        builder.setView(newPasswordEditText)

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            val newPassword = newPasswordEditText.text.toString().trim()

            // Perform password validation logic based on the entered new password

            if (validateNewPassword(newPassword)) {
                // Password change successful
                val database = FirebaseDatabase.getInstance()
                val adminsRef = database.getReference("admin").child("11").child("password")
                adminsRef.setValue(newPassword)
                showPasswordChangeSuccessDialog()
            } else {
                // Invalid new password
                showInvalidPasswordDialog()
            }

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            // User canceled the password change
            // You can perform any actions here
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun validateNewPassword(newPassword: String): Boolean {
        // Perform validation logic for the new password here
        // You can implement any rules or checks for the new password
        // Return true if the new password is valid, false otherwise
        // Replace this placeholder implementation with your actual validation logic
        val pattern = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}")
        val matcher = pattern.matcher(newPassword)
        return matcher.matches()
    }
    private fun showPasswordChangeSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Password Change Success")
        builder.setMessage("Your password has been changed successfully.")

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            // User acknowledged the password change success
            // You can perform any actions here
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun showIncorrectPasswordDialog() {
        //val alertDialogBuilder = AlertDialog.Builder(this)
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setTitle("Incorrect Password")
        alertDialogBuilder.setMessage("The entered old password is incorrect.")

        alertDialogBuilder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            // User acknowledged the incorrect password
            // You can perform any actions here
            dialogInterface.dismiss()
        }

        val dialog = alertDialogBuilder.create()
        dialog.show()
    }
    private fun showInvalidPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Invalid Password")
        builder.setMessage("The entered new password is invalid. Please make sure it has at least one uppercase letter and one special character anc Contains at least 8 characters.")

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            // User acknowledged the invalid password
            // You can perform any actions here
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Log Out")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { dialog, which -> // Perform logout actions and navigate to another page
            performLogout()
        }
        builder.setNegativeButton("No", null)
        val dialog = builder.create()
        dialog.show()
    }
    private fun performLogout() {
        val database = FirebaseDatabase.getInstance()
        val sessionRef = database.getReference("Sessions")
        val deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        val query = sessionRef.orderByChild("deviceId").equalTo(deviceId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (sessionSnapshot in dataSnapshot.children) {
                    sessionSnapshot.ref.removeValue()
                }
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }



    private fun comparePasswordWithFirebase(password: String, callback: (Boolean) -> Unit) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val collectionRef: DatabaseReference = database.getReference("admin")
        val query = collectionRef.orderByChild("password").equalTo(password)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var match = false
                for (snapshot in dataSnapshot.children) {
                    if (password == snapshot.child("password").getValue(String::class.java)) {
                        match = true
                        break
                    }
                }
                callback.invoke(match)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the database error, if necessary
                callback.invoke(false)
            }
        })
    }
}