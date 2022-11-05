package com.example.area.fragment.area

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.area.AREAApplication
import com.example.area.MainViewModel
import com.example.area.MainViewModelFactory
import com.example.area.R
import com.example.area.activity.AreaActivity
import com.example.area.adapter.ServiceItemAdapter
import com.example.area.data.ActionReactionDatasource
import com.example.area.data.ServiceDatasource
import com.example.area.model.about.About
import com.example.area.model.about.AboutClass
import com.example.area.repository.Repository
import com.example.area.utils.SessionManager

class AreaCreationActionServiceFragment : Fragment(R.layout.fragment_area_creation_action_service) {
    private var serviceSelectedIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = super.onCreateView(inflater, container, savedInstanceState) ?: return null
        val aboutClass = ((context as AreaActivity).application as AREAApplication).aboutClass ?: return view
        val servicesImages = ((context as AreaActivity).application as AREAApplication).aboutBitmapList ?: return view
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewActionService)
        val serviceList = ServiceDatasource()

        recycler.layoutManager = LinearLayoutManager(context as AreaActivity)
        serviceList.clear()
        for (elem in aboutClass.getServiceList()) {
            serviceList.addService(elem.id, elem.name, servicesImages[elem.id - 1])
        }
        updateRecycler(recycler, serviceList)
        view.findViewById<Button>(R.id.backFromActionServiceCreationButton).setOnClickListener {
            (context as AreaActivity).onBackPressed()
        }
        view.findViewById<Button>(R.id.nextFromActionServiceCreation).setOnClickListener {
            if (serviceSelectedIndex != -1) {
                (context as AreaActivity).changeFragment(AreaCreationActionFragment(serviceList.loadServiceInfo()[serviceSelectedIndex]), "action_creation")
            } else {
                Toast.makeText(context as AreaActivity, "Please select an action service", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun updateRecycler(recycler: RecyclerView, serviceList: ServiceDatasource) {
        recycler.setHasFixedSize(true)
        recycler.adapter = ServiceItemAdapter(context as AreaActivity, serviceList.loadServiceInfo()) { position -> onItemClick(position, serviceList.loadServiceInfo()[position].name) }
    }

    private fun onItemClick(position: Int, toPrint: String) {
        serviceSelectedIndex = position
        Toast.makeText(context as AreaActivity, "$toPrint selected", Toast.LENGTH_SHORT).show()
    }
}