import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studybuddy.R
import com.example.studybuddy.data.Request
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Context.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog



class RequestAdapter : ListAdapter<Request, RequestAdapter.RequestViewHolder>(RequestDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.request_item, parent, false)
        return RequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        //this function for each individual view item
        val currentRequest = getItem(position)
        if (currentRequest.requeststate == 1) {
            holder.bind(currentRequest)
        } else {

            holder.itemView.visibility = View.GONE
        }
    }

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        private val declineButton: Button = itemView.findViewById(R.id.declineButton)
        private val infoButton: ImageButton = itemView.findViewById(R.id.infoButton)

        fun bind(request: Request) {
                titleTextView.text = request.requestTitle
                descriptionTextView.text = request.requestDescription

            acceptButton.setOnClickListener {
                //when clicked it will change the state from 1 "Pending" to 2 "Accepted"
                val database = FirebaseDatabase.getInstance()
                val childRef = database.reference.child("requests")
                val requestId = request.requestId
                val query = childRef.orderByKey().equalTo(requestId)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (childSnapshot in dataSnapshot.children) {
                                val childRef = childSnapshot.ref
                                childRef.child("requeststate").setValue(2)


                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any potential errors
                    }
                })
            }

            declineButton.setOnClickListener {
                //when clicked it will change the state from 1 "Pending" to 0 "Declined"
                val database = FirebaseDatabase.getInstance()
                val childRef = database.reference.child("requests")
                val requestId = request.requestId
                val query = childRef.orderByKey().equalTo(requestId)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (childSnapshot in dataSnapshot.children) {
                                val childRef = childSnapshot.ref
                                childRef.child("requeststate").setValue(0)
                                Toast.makeText(itemView.context, "Demo data created", Toast.LENGTH_SHORT).show()


                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any potential errors
                    }
                })
            }

            infoButton.setOnClickListener {
                //it gets the info of requester based on his key that have been sent by him in the request to tech activity that the user have
                val database = FirebaseDatabase.getInstance()
                val childRef = database.reference.child("users")
                val requesterkey = request.requesterkey
                val query = childRef.orderByKey().equalTo(requesterkey)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (childSnapshot in dataSnapshot.children) {
                                val email = childSnapshot.child("email").getValue(String::class.java)
                                val major = childSnapshot.child("major").getValue(String::class.java)
                                val numberofstars = childSnapshot.child("numberofstars").getValue(Double::class.java)
                                val numberofraters = childSnapshot.child("numberofraters").getValue(Double::class.java)
                                var rating = numberofstars?.div(numberofraters!!)
                                val message = "Email: $email\nMajor: $major\nRating: $rating"

                                val alertDialog = AlertDialog.Builder(itemView.context)
                                    .setTitle("User Details")
                                    .setMessage(message)
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()
                                alertDialog.show()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any potential errors
                    }
                })
            }

        }
    }
}


class RequestDiffCallback : DiffUtil.ItemCallback<Request>() {
    //so it does not show the same request twice
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {

        return oldItem.requestId == newItem.requestId
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }
}
