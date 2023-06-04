import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studybuddy.Activity.UserAddCoursesActivity
import com.example.studybuddy.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.UUID



class UserSemestersManagerFragment : Fragment() {

    private lateinit var addedSemesterButton: Button
    private lateinit var rootView2: View
    private lateinit var rootView: View

    private lateinit var semesterOptions: Array<String>
    private lateinit var editText: EditText
    private lateinit var selectedSemester: String

    private lateinit var email: String
    private lateinit var usersRef: DatabaseReference


    private var emailPopupShown = false
    /*
        if the emailPopupShown is true in firebase no email popup should shows and app take the email from session
        # anyone do it
    */
    var checker: Int = 0


    //showSemPopup
    private val semesterInfoList = mutableListOf<SemesterInfo>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SemesterAdapter
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private val courseList: MutableList<Course> = mutableListOf()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_user_semesters_manager, container, false)
        rootView2 = inflater.inflate(R.layout.user_semester_popup, container, false)

        editText = rootView2.findViewById(R.id.SemesterYear)
        semesterOptions = resources.getStringArray(R.array.semester_options)

        firebaseAuth = FirebaseAuth.getInstance()


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addedSemesterButton = view.findViewById(R.id.semesterFragBtn)
        addedSemesterButton.setOnClickListener { showSemesterPopup() }

        /*addedSemesterButton.setOnClickListener {
            if (!emailPopupShown) {
                checker = 1 // for showSemester Form
                showEmailPopup()
                emailPopupShown = true
            } else {
                showSemesterPopup()
            }
        }*/
        val showSemsText = view.findViewById<TextView>(R.id.ViewSemester)

        /*showSemsText.setOnClickListener {
            if (!emailPopupShown) {
                checker = 2 // to show Semester Info
                showEmailPopup()
                emailPopupShown = true
            } else {
                showSem()
            }
        }*/

        showSemsText.setOnClickListener { showSem() }

        val database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
    }

    @SuppressLint("MissingInflatedId")
    /*    private fun showEmailPopup() {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            val emailPopupView = LayoutInflater.from(requireContext()).inflate(R.layout.email_popup, null)
            val emailPopupWindow = PopupWindow(emailPopupView, screenWidth, screenHeight, true)

            val emailEditText = emailPopupView.findViewById<EditText>(R.id.emailEditText)
            val continueButton = emailPopupView.findViewById<Button>(R.id.continueButton)

            continueButton.setOnClickListener {
                email = emailEditText.text.toString()
                emailPopupWindow.dismiss()
                if(checker == 1) {
                    showSemesterPopup()
                }else {
                    showSem()
                }

            }

            emailPopupWindow.setOnDismissListener {
                emailEditText.text.clear()
            }

            emailPopupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
        }
    */
    private fun showSemesterPopup() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val semesterPopupView = LayoutInflater.from(requireContext()).inflate(R.layout.user_semester_popup, null)
        val semesterPopupWindow = PopupWindow(semesterPopupView, screenWidth, screenHeight, true)

        val editText = semesterPopupView.findViewById<EditText>(R.id.SemesterYear)






        val addButton = semesterPopupView.findViewById<Button>(R.id.AddSemesterButton)
        val spinner = semesterPopupView.findViewById<Spinner>(R.id.SemesterSpinner)
        val semesterRef = FirebaseDatabase.getInstance().getReference("Semester")
        val usersRef = FirebaseDatabase.getInstance().getReference("users")


        // Set adapter for the Spinner
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            semesterOptions
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSemester = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle when nothing is selected
            }
        }

        addButton.setOnClickListener {
            val semesterValue = editText.text.toString()
            //create uniqe id
            val uniqueIdString = "$selectedSemester - $semesterValue"
            val semesterInfo = SemesterInfo(semesterValue, selectedSemester, uniqueIdString)

            // Generate a unique key for the semester
            val semesterKey = semesterRef.push().key

            // Get user email
            val userEmail = GlobalData.userEmail

            // Add the semester to the user's table using the unique key
            val userQuery = usersRef.orderByChild("email").equalTo(userEmail)
            userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userKey = userSnapshot.key
                        if (userKey != null) {
                            usersRef.child(userKey).child("semester").child(semesterKey!!).setValue(semesterInfo)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors
                }
            })

            semesterPopupWindow.dismiss()
        }

        semesterPopupWindow.setOnDismissListener {
            editText.text.clear()
        }

        semesterPopupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
    }

    private fun retrieveCoursesBySemesterId(uniqueSemesterId: String) {
        val semesterRef = FirebaseDatabase.getInstance().getReference("users")
            .child(uniqueSemesterId)
            .child("courses")

        semesterRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val uniqueCourseIds = HashSet<String>() // HashSet to store unique course IDs
                val newCourseList = mutableListOf<Course>() // Temporary list for newly retrieved courses

                for (courseSnapshot in dataSnapshot.children) {
                    val courseId = courseSnapshot.key // Get the course ID

                    if (courseId != null && !uniqueCourseIds.contains(courseId)) {
                        val courseName = courseSnapshot.child("name").getValue(String::class.java)
                        val courseGrade = courseSnapshot.child("grade").getValue(String::class.java)

                        if (courseName != null && courseGrade != null) {
                            val course = Course(courseName, "", "", courseGrade, null)
                            newCourseList.add(course)

                            uniqueCourseIds.add(courseId) // Add the course ID to the set
                        }
                    }
                }

                // Update the courses list in the corresponding SemesterInfo object
                for (semesterInfo in semesterInfoList) {
                    if (semesterInfo.uniqeId == uniqueSemesterId) {
                        semesterInfo.courses.clear()
                        semesterInfo.courses.addAll(newCourseList)
                        break
                    }
                }

                // Update your UI or perform any necessary actions with the retrieved courses
                // For example, you can update a RecyclerView adapter with the new semesterInfoList
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during the database retrieval
            }
        })
    }






    private fun showSem() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // to get each id in layout
        val showsems = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_user_semesters, null)
        val semesterPopupWindow = PopupWindow(showsems, screenWidth, screenHeight, true)

        //Get user email
        val userEmail = GlobalData.userEmail






        /*
            Firebase connection
        */

        recyclerView = showsems.findViewById(R.id.semesterRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SemesterAdapter(semesterInfoList)
        recyclerView.adapter = adapter

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().reference.child("users")

        // Add a ValueEventListener to retrieve data from Firebase database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                semesterInfoList.clear()



                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)

                    // Check if the user's email matches the desired email
                    if (user?.email == userEmail) {
                        val semesterDataSnapshot = userSnapshot.child("semester")

                        for (semesterSnapshot in semesterDataSnapshot.children) {
                            val semesterNumber = semesterSnapshot.child("semesterNumber").value as? String
                            val selectedSemester = semesterSnapshot.child("selectedSemester").value as? String

                            //create uniqe id
                            val uniqueIdString = "$selectedSemester - $semesterNumber"

                            retrieveCoursesBySemesterId(uniqueIdString)

                            if (semesterNumber != null && selectedSemester != null) {
                                val semesterInfo = SemesterInfo(semesterNumber, selectedSemester,uniqueIdString)
                                semesterInfoList.add(semesterInfo)
                            }
                        }
                        break // Break the loop once the desired user is found
                    }


                }



                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })


        /*
            END Conn..
        */

        semesterPopupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
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
            private val courseListTextView: TextView = itemView.findViewById(R.id.courseListTextView)

            //to access the add course button
            private val addCourseBtn: FloatingActionButton = itemView.findViewById(R.id.addCourse)

            fun bind(semesterInfo: SemesterInfo) {
                semesterNumberTextView.text = "${semesterInfo.semesterNumber} - ${semesterInfo.selectedSemester}"
                courseListTextView.text = "" // Clear the courseListTextView


                for (course in semesterInfo.courses) {
                    courseListTextView.append("${course.name}: ${course.grade}\n\n")
                }


                //TODO: to redirect to course page
                /*
                    1- when the user press the button he goes to another page and add the course from 2 spinner.
                         spinners: one for major and one for course.
                    2- when the data is filled, it's saved in firebase in semester as object call courses.
                        saved with the semester year and season.
                    3- when I want to retrieve the semester's courses I fetched it with the semester info.
                */
                addCourseBtn.setOnClickListener {
                    println("Clicked on item: $adapterPosition")
                    // You can access the specific semesterInfo using the adapterPosition
                    // Perform any desired actions with the clicked semesterInfo
                    val clickedSemesterInfo = semesterInfoList[adapterPosition]

                    val intent = Intent(itemView.context, UserAddCoursesActivity::class.java)
                    intent.putExtra("semesterNumber", clickedSemesterInfo.semesterNumber)
                    intent.putExtra("selectedSemester", clickedSemesterInfo.selectedSemester)
                    intent.putExtra("uniqeSemesterId", clickedSemesterInfo.uniqeId)
                    itemView.context.startActivity(intent)
                }
            }


        }
    }
}
data class User(
    val email: String = "",
    val semester: Map<String, SemesterInfo> = emptyMap()
)
data class SemesterInfo(
    var semesterNumber: String = "",
    var selectedSemester: String = "",
    var uniqeId: String = "",
    val courses: MutableList<Course> = mutableListOf()
) {
    fun addCourse(course: Course) {
        // Check if a course with the same name and grade already exists
        val existingCourse = courses.find { it.name == course.name && it.grade == course.grade }

        if (existingCourse != null) {
            // Update the existing course's grade
            existingCourse.grade = course.grade
        } else {
            // Add the new course to the list
            courses.add(course)
        }
    }
}
