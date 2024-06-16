package com.example.musicalgames.games.sight_sing


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicalgames.R
import kotlin.random.Random

class ViolinStaffFragment : Fragment() {
    private var staff: StaffView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_violin_staff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notePositions = generateRandomNotePositions()
        staff = view.findViewById(R.id.staffView)
        staff!!.setNotePositions(notePositions)
    }

    private fun generateRandomNotePositions(): List<Float> {
        val positions = listOf(50f, 100f, 150f, 200f, 250f, 300f)
        return List(3) { positions[Random.nextInt(positions.size)] }
    }
}
