import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.studybuddy.R
import com.google.firebase.database.FirebaseDatabase

class UserSemestersManagerFragment : Fragment() {

    private lateinit var addedSemesterButton: Button
    private lateinit var rootView2: View
    private lateinit var rootView: View
    private lateinit var semesterOptions: Array<String>
    private lateinit var editText: EditText
    private lateinit var selectedSemester: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_user_semesters_manager, container, false)
        rootView2 = inflater.inflate(R.layout.user_semester_popup, container, false)
        editText = rootView2.findViewById(R.id.SemesterYear)
        semesterOptions = resources.getStringArray(R.array.semester_options)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addedSemesterButton = view.findViewById(R.id.semesterFragBtn)
        addedSemesterButton.setOnClickListener { showPopup() }
    }

    private fun showPopup() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val popupWindow = PopupWindow(rootView2, screenWidth, screenHeight, true)

        val addButton = rootView2.findViewById<Button>(R.id.AddSemesterButton)
        val spinner = rootView2.findViewById<Spinner>(R.id.SemesterSpinner)
        val database = FirebaseDatabase.getInstance()
        val semesterRef = database.getReference("Semester")

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
            val semesterInfo = SemesterInfo(semesterValue, selectedSemester)
            val newSemRef = semesterRef.push()
            newSemRef.setValue(semesterInfo)
            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
            editText.text.clear()
        }

        popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
    }
}

data class SemesterInfo(val semesterNumber: String, val selectedSemester: String)
