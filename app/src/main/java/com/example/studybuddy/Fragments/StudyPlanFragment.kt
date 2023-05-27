package com.example.studybuddy.Fragments

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.studybuddy.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StudyPlanFragment : AppCompatActivity() {
    private lateinit var listView: ListView
    private var arrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_study_plan)
        val customToolbar = findViewById<TextView>(R.id.action_bar_title)
        customToolbar.text = intent.getStringExtra("majorName")

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener() {
            finish()
        }

        listView = findViewById(R.id.studyplanListView)

        val majorName = intent.getStringExtra("majorName")
        val majorsRef = FirebaseDatabase.getInstance().getReference("majors")
        majorsRef.orderByChild("name").equalTo(majorName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (majorSnapshot in dataSnapshot.children) {
                    println("xd")
                    val studyPlansRef = majorSnapshot.child("Study Plans").ref
                    studyPlansRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(studyPlanDataSnapshot: DataSnapshot) {
                            arrayList.clear()
                            for (studyPlanSnapshot in studyPlanDataSnapshot.children) {
                                val studyPlanName = studyPlanSnapshot.child("name").getValue(String::class.java)
                                studyPlanName?.let { arrayList.add(it) }
                            }

                            val adapter = ArrayAdapter(this@StudyPlanFragment, android.R.layout.simple_list_item_1, arrayList)
                            listView.adapter = adapter
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle any errors that occur
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
            }
        })
    }
}


