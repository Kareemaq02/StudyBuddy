package com.example.studybuddy.Fragments
import RequestAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studybuddy.R
import android.widget.Toast
import com.example.studybuddy.data.Request
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var requestsRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        requestAdapter = RequestAdapter()
        recyclerView.adapter = requestAdapter
        database = FirebaseDatabase.getInstance()
        requestsRef = database.getReference("requests")

        //createDemoData()

        fetchRequests()

        return view
    }

    private fun fetchRequests() {
        requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //Saving the Data that we get from firebase
                val requests = mutableListOf<Request>()
                for (snapshot in dataSnapshot.children) {
                    val request = snapshot.getValue(Request::class.java)
                    request?.let {
                        it.requestId = snapshot.key // overwrite the requestis so it store the requestkey
                        requests.add(it)
                    }
                }
                requestAdapter.submitList(requests)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error: " + databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }





    //this function will be in the user side because he will Create the Requst to teach
    private fun createDemoData() {
        // Generate demo requests
        val request1 = Request("1", "Request abedddddddd", requestDescription = "testing testing testing", requeststate =  1,requesterkey = "-NVqF6tqimDRp9Az-l60F" )
        val request2 = Request("2", "Request 22222222222", requestDescription = "testing testing testing", requeststate =  1,requesterkey = "-NVqF6tqimDRp9Az-l6F" )
        val request3 = Request("2", "Request 33333333333", requestDescription = "testing testing testing", requeststate =  1,requesterkey = "-NVqF6tqimDRp9Az-l6F" )

        // Insert demo requests into Firebase
        requestsRef.push().setValue(request1)
        requestsRef.push().setValue(request2)
        requestsRef.push().setValue(request3)

        Toast.makeText(context, "Demo data created", Toast.LENGTH_SHORT).show()
    }
}
