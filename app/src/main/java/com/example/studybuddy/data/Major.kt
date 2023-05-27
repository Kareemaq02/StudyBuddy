package com.example.studybuddy.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Major(var name: String) {
    var id: String? = null

    fun save() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val majorsRef: DatabaseReference = database.getReference("majors")
        id = majorsRef.push().key
        majorsRef.child(id!!).setValue(this)
    }

}