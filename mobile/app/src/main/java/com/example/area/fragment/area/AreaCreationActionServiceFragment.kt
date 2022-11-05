package com.example.area.fragment.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.area.AREAApplication
import com.example.area.MainViewModel
import com.example.area.R
import com.example.area.activity.AreaActivity
import com.example.area.adapter.ServiceItemAdapter
import com.example.area.data.ServiceDatasource

class AreaCreationActionServiceFragment : Fragment(R.layout.fragment_area_creation_action_service) {
    private var serviceSelectedIndex: Int = -1
    private lateinit var serviceAdapter: ServiceItemAdapter

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
            if (elem.actions.isNotEmpty())
                serviceList.addService(elem.id, elem.name, servicesImages[elem.id - 1])
        }
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
        view.findViewById<SearchView>(R.id.searchActionService).setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(toSearch: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(toSearch: String?): Boolean {
                serviceAdapter.filter(toSearch)
                return false
            }
        })
        updateRecycler(recycler, serviceList)
        return view
    }

    private fun updateRecycler(recycler: RecyclerView, serviceList: ServiceDatasource) {
         serviceAdapter = ServiceItemAdapter(
            context as AreaActivity,
            serviceList.loadServiceInfo()
        ) { position -> onItemClick(position, serviceList.loadServiceInfo()[position].name) }
        recycler.setHasFixedSize(true)
        recycler.adapter = serviceAdapter
    }

    private fun onItemClick(position: Int, toPrint: String) {
        serviceSelectedIndex = position
        Toast.makeText(context as AreaActivity, "$toPrint selected", Toast.LENGTH_SHORT).show()
    }
}