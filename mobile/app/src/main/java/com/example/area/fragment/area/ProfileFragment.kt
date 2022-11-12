package com.example.area.fragment.area

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.area.AREAApplication
import com.example.area.MainViewModel
import com.example.area.R
import com.example.area.activity.AreaActivity
import com.example.area.activity.MainActivity
import com.example.area.utils.SessionManager
import com.google.android.material.textview.MaterialTextView

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) ?: return null
        return view
    }


}