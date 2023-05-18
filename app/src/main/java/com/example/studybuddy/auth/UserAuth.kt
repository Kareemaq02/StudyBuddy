package com.example.studybuddy.auth
import android.content.ContentResolver
import android.provider.Settings
import com.example.studybuddy.data.User
import com.google.firebase.database.*


object UserAuth {
    private var loggedInUser: User? = null

    fun isLoggedIn(contentResolver: ContentResolver, callback: (Boolean) -> Unit) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val collectionRef: DatabaseReference = database.getReference("Sessions")
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val query = collectionRef.orderByChild("deviceId").equalTo(deviceId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isLoggedIn = dataSnapshot.exists()
                callback.invoke(isLoggedIn)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the database error, if necessary
                callback.invoke(false)
            }
        })
    }


    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        // Perform the login process and authenticate the user
        // Set the loggedInUser if login is successful
        // Invoke the callback with the result (true for success, false for failure)
        // You can replace the example implementation with your authentication logic
        if (email == "example@example.com" && password == "password123") {
            loggedInUser = User(email, password, "John", "Doe", "Computer Science")
            callback.invoke(true)
        } else {
            callback.invoke(false)
        }
    }

    fun logout() {
        // Clear the loggedInUser to indicate that the user is logged out
        loggedInUser = null
    }
}
