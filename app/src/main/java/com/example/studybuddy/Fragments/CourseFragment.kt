
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.studybuddy.Fragments.StudyPlanFragment
import com.example.studybuddy.R
import com.example.studybuddy.data.Major
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class CourseFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val arrayList = ArrayList<String>()
    private lateinit var button: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_course, container, false)
        listView = view.findViewById(R.id.studyplanListView)

        val database = FirebaseDatabase.getInstance()
        val majorRef = database.getReference("majors")


        majorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                arrayList.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val majorName = childSnapshot.child("name").getValue(String::class.java)
                    arrayList.add(majorName.toString())
                }

                // Create the custom adapter
                adapter = object : ArrayAdapter<String>(
                    requireContext(),
                    R.layout.list_view_item, // custom list item layout
                    R.id.itemTextView, // TextView within the custom layout to populate with data
                    arrayList
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)

                        return view
                    }
                }

                listView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedMajor = arrayList[position]
            val majorName = selectedMajor
            val intent = Intent(requireContext(), StudyPlanFragment::class.java)
            intent.putExtra("majorName", majorName)

            // Retrieve the study plan data for the selected major
            val studyPlanNames = mutableListOf<String>()

            val majorsRef = FirebaseDatabase.getInstance().getReference("majors")
            majorsRef.orderByChild("name").equalTo(majorName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (majorSnapshot in dataSnapshot.children) {
                        val studyPlansRef = majorSnapshot.child("studyPlans").ref
                        studyPlansRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(studyPlanDataSnapshot: DataSnapshot) {
                                for (studyPlanSnapshot in studyPlanDataSnapshot.children) {
                                    val studyPlanName = studyPlanSnapshot.child("name").getValue(String::class.java)
                                    studyPlanName?.let { studyPlanNames.add(it) }
                                }

                               intent.putExtra("studyPlans",ArrayList(studyPlanNames))
                                startActivity(intent)
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


        button = view.findViewById(R.id.addMajorButton)
        button.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_major_admin, null)
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Add Major")

            val dialog = dialogBuilder.create()
            dialog.show()

            val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
            val majorNameEditText = dialogView.findViewById<EditText>(R.id.majorNameEditText)

            confirmButton.setOnClickListener {
                val majorName = majorNameEditText.text.toString()
                if (majorName!="") {
                    val majorData = Major(majorName)
                    val newMajorRef = majorRef.push()
                    newMajorRef.child("name").setValue(majorData.name)
                    dialog.dismiss()
                }
                else
                { val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle("Invalid Major Name")
                        .setMessage("Major name cannot be empty")
                        .setPositiveButton("OK", null)
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()}

            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }



        return view
    }
}