import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studybuddy.R
import com.google.firebase.database.*

class UserSemesters : Fragment() {

    private val semesterInfoList = mutableListOf<SemesterInfo>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SemesterAdapter
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_semesters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.semesterRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SemesterAdapter(semesterInfoList)
        recyclerView.adapter = adapter

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().reference.child("Semester")

        // Add a ValueEventListener to retrieve data from Firebase database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                semesterInfoList.clear()

                for (snapshot in dataSnapshot.children) {
                    val semesterNumber = snapshot.child("semesterNumber").value as? String
                    val selectedSemester = snapshot.child("selectedSemester").value as? String

                    if (semesterNumber != null && selectedSemester != null) {
                        val semesterInfo = SemesterInfo(semesterNumber, selectedSemester)
                        semesterInfoList.add(semesterInfo)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    private class SemesterAdapter(private val semesterInfoList: List<SemesterInfo>) :
        RecyclerView.Adapter<SemesterAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_semester_info, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val semesterInfo = semesterInfoList[position]
            holder.bind(semesterInfo)
        }

        override fun getItemCount(): Int {
            return semesterInfoList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val semesterNumberTextView: TextView = itemView.findViewById(R.id.semesterNumberTextView)
            fun bind(semesterInfo: SemesterInfo) {
                semesterNumberTextView.text = "${semesterInfo.semesterNumber} - ${semesterInfo.selectedSemester}"
            }
        }
    }
}
