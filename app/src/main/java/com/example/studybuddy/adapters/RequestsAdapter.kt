package com.example.studybuddy.adapters

import GlobalData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studybuddy.R
import com.example.studybuddy.data.learnRequest
import com.example.studybuddy.data.teachRequest
import com.google.firebase.database.*

class RequestsAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_LEARN_REQUEST = 0
        private const val VIEW_TYPE_TEACH_REQUEST = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LEARN_REQUEST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_learn_request, parent, false)
                LearnRequestViewHolder(view)
            }
            VIEW_TYPE_TEACH_REQUEST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teach_request, parent, false)
                TeachRequestViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is LearnRequestViewHolder -> {
                val learnRequest = item as learnRequest
                holder.bind(learnRequest)
            }
            is TeachRequestViewHolder -> {
                val teachRequest = item as teachRequest
                holder.bind(teachRequest)
            }
            else -> throw IllegalArgumentException("Invalid view holder")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is learnRequest -> VIEW_TYPE_LEARN_REQUEST
            is teachRequest -> VIEW_TYPE_TEACH_REQUEST
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    // View holder for LearnRequestItem
    class LearnRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: learnRequest) {
            val firstNameTextView: TextView = itemView.findViewById(R.id.textViewFirstName2)
            val lastNameTextView: TextView = itemView.findViewById(R.id.textViewLastName2)
            val majorTextView: TextView = itemView.findViewById(R.id.textViewMajor2)
            val courseTextView: TextView = itemView.findViewById(R.id.textViewCourseName2)
            val preferredTimeTextView: TextView = itemView.findViewById(R.id.textViewPreferredTime)
            val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription2)
            val upVotesCountTextView: TextView = itemView.findViewById(R.id.textViewUpvotesCount)

            val userKey = item.userId


            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val usersRef: DatabaseReference = database.getReference("users")


            val userFirstNameRef: DatabaseReference = usersRef.child(userKey).child("firstname")
            userFirstNameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val userFirstName: String? = dataSnapshot.getValue(String::class.java)


                    if (userFirstName != null) {
                        firstNameTextView.text = userFirstName
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })


            val userLastNameRef: DatabaseReference = usersRef.child(userKey).child("lastname")
            userLastNameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val userLastName: String? = dataSnapshot.getValue(String::class.java)


                    if (userLastName != null) {
                        lastNameTextView.text = userLastName
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            majorTextView.text = item.major_Name
            courseTextView.text = item.course_Name
            preferredTimeTextView.text = item.pref_time
            descriptionTextView.text = item.requestDescription
            upVotesCountTextView.text = item.upvoters.toString()













        }





    }

    // View holder for TeachRequest
    class TeachRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: teachRequest) {
            val firstNameTextView: TextView = itemView.findViewById(R.id.textViewFirstName)
            val lastNameTextView: TextView = itemView.findViewById(R.id.textViewLastName)
            val majorTextView: TextView = itemView.findViewById(R.id.textViewMajor)
            val courseTextView: TextView = itemView.findViewById(R.id.textViewCourseName)
            val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
            val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
            val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)
            val placeTextView: TextView = itemView.findViewById(R.id.textViewPlace)
            val participantsCountTextView: TextView = itemView.findViewById(R.id.textViewParticipantsCount)
            val userKey = item.userId


            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val usersRef: DatabaseReference = database.getReference("users")


            val userFirstNameRef: DatabaseReference = usersRef.child(userKey).child("firstname")
            userFirstNameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val userFirstName: String? = dataSnapshot.getValue(String::class.java)


                    if (userFirstName != null) {
                        firstNameTextView.text = userFirstName
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })


            val userLastNameRef: DatabaseReference = usersRef.child(userKey).child("lastname")
            userLastNameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val userLastName: String? = dataSnapshot.getValue(String::class.java)


                    if (userLastName != null) {
                        lastNameTextView.text = userLastName
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            majorTextView.text = item.major_Name
            courseTextView.text = item.course_Name
            descriptionTextView.text = item.lessonDescription
            dateTextView.text = formatDate(item.start_date)
            val time= "${item.start_time} - ${item.end_time}"
            timeTextView.text = time
            placeTextView.text = item.place
            participantsCountTextView.text = item.number_of_students.toString()

           val participateButton: Button = itemView.findViewById(R.id.buttonParticipate)

            participateButton.setOnClickListener {
                val requestRef: DatabaseReference = database.getReference("requests").child(item.requestId.toString())
                val currentUserId = GlobalData.loggedInUserId

                requestRef.child("participantsIds").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val participantsList: MutableList<String> = if (dataSnapshot.exists()) {
                            dataSnapshot.getValue(object : GenericTypeIndicator<MutableList<String>>() {})
                                ?: mutableListOf()
                        } else {
                            mutableListOf()
                        }

                        if (item.number_of_students != item.studentsLimit) {
                            // Check if the current user is already in the participants list
                            if (!participantsList.contains(currentUserId)) {
                                // Add the current user to the participants list
                                participantsList.add(currentUserId)

                                // Update the participants list in the database
                                requestRef.child("participantsIds").setValue(participantsList)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val participantsCount: Int = item.number_of_students + 1
                                            requestRef.child("number_of_students").setValue(participantsCount)
                                            participantsCountTextView.text = "$participantsCount"
                                            Toast.makeText(itemView.context, "You have successfully participated", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(itemView.context, "Failed to participate in the request", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(itemView.context, "You are already a participant in this class", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(itemView.context, "Class has already reached participants limit", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle database error
                    }
                })


            }






        }
        private fun formatDate(dateString: String): String {
            val parts = dateString.split(" ")
            val day = parts[0]
            val month = parts[1]
            val year = parts[2]
            return "$month/$day/$year"
        }
    }


}
