package com.kulipai.luahook
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.home, container, false)
        val card1:MaterialCardView by lazy { view.findViewById(R.id.card1) }

        card1.setOnClickListener{
            val intent = Intent(requireActivity(), EditActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}