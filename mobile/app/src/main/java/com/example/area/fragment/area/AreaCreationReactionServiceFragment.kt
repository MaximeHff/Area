package com.example.area.fragment.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.area.MainViewModel
import com.example.area.MainViewModelFactory
import com.example.area.R
import com.example.area.activity.AreaActivity
import com.example.area.adapter.ServiceItemAdapter
import com.example.area.data.ServiceDatasource
import com.example.area.model.ActionReactionInfo
import com.example.area.model.ServiceInfo
import com.example.area.model.about.About
import com.example.area.repository.Repository
import com.example.area.utils.AboutJsonCreator
import com.example.area.utils.SessionManager

class AreaCreationReactionServiceFragment(private val actionService: ServiceInfo, private val action: ActionReactionInfo) : Fragment(R.layout.fragment_area_creation_reaction_service) {
    private lateinit var viewModel: MainViewModel
    private var abt: About? = null
    private var serviceSelectedIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = super.onCreateView(inflater, container, savedInstanceState) ?: return null
        val sessionManager = SessionManager(context as AreaActivity)
        val about = AboutJsonCreator()
        val rep = Repository(sessionManager.fetchAuthToken("url")!!)
        val viewModelFactory = MainViewModelFactory(rep)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewReactionService)
        val serviceList = ServiceDatasource()

        recycler.layoutManager = LinearLayoutManager(context as AreaActivity)
        recycler.setHasFixedSize(true)
        recycler.adapter = ServiceItemAdapter(
            context as AreaActivity,
            serviceList.loadServiceInfo()
        ) { position -> onItemClick(position) }
        view.findViewById<Button>(R.id.backFromReactionServiceCreationButton).setOnClickListener {
            (context as AreaActivity).onBackPressed()
        }
        view.findViewById<Button>(R.id.nextFromReactionServiceCreation).setOnClickListener {
            if (serviceSelectedIndex != -1) {
                (context as AreaActivity).changeFragment(AreaCreationReactionFragment(actionService, action, serviceList.loadServiceInfo()[serviceSelectedIndex]), "reaction_creation")
            } else {
                Toast.makeText(context as AreaActivity, "Please select an reaction service", Toast.LENGTH_SHORT).show()
            }
        }
        about.getAboutJson(context as AreaActivity, this, this) {
            abt = about.liveDataResponse.value
            if (abt != null) {
                serviceList.clear()
                for (elem in abt!!.server.services) {
                    serviceList.addService(elem.id, elem.name, elem.imageUrl)
                }
                recycler.setHasFixedSize(true)
                recycler.adapter = ServiceItemAdapter(
                    context as AreaActivity,
                    serviceList.loadServiceInfo()
                ) { position -> onItemClick(position) }
            }
        }
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        return view
    }

    private fun onItemClick(position: Int) {
        serviceSelectedIndex = position
    }
}