package com.example.area.fragment.area

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.area.AREAApplication
import com.example.area.MainViewModel
import com.example.area.MainViewModelFactory
import com.example.area.R
import com.example.area.activity.AreaActivity
import com.example.area.activity.OAuthLinkingActivity
import com.example.area.adapter.MarginItemDecoration
import com.example.area.adapter.ServiceItemAdapter
import com.example.area.data.ServiceDatasource
import com.example.area.model.ServiceListElement
import com.example.area.model.about.AboutClass
import com.example.area.repository.Repository
import com.example.area.utils.SessionManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

class AreaCreationActionServiceFragment : Fragment(R.layout.fragment_area_creation_action_service) {
    private lateinit var serviceAdapter: ServiceItemAdapter
    val serviceList = ServiceDatasource()

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

        recycler.layoutManager = GridLayoutManager(context as AreaActivity, 2)
        feelSearchedList(serviceList, feelEntireList(aboutClass, servicesImages))
        view.findViewById<SearchView>(R.id.searchActionService).setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(toSearch: String?): Boolean {
                onQuery(toSearch, aboutClass, servicesImages, serviceList, recycler)
                return false
            }
            override fun onQueryTextChange(toSearch: String?): Boolean {
                onQuery(toSearch, aboutClass, servicesImages, serviceList, recycler)
                return false
            }
        })
        updateRecycler(recycler, serviceList)
        recycler.addItemDecoration(MarginItemDecoration(50))
        return view
    }

    private fun onQuery(toSearch: String?, aboutClass: AboutClass, servicesImages: List<Bitmap>, serviceList: ServiceDatasource, recycler: RecyclerView) {
        serviceAdapter.filter(toSearch, feelEntireList(aboutClass, servicesImages))
        serviceList.clear()
        feelSearchedList(serviceList, serviceAdapter.getDataset())
        updateRecycler(recycler, serviceList)
    }

    private fun feelSearchedList(serviceList: ServiceDatasource, toFeedList: List<ServiceListElement>) {
        serviceList.clear()
        for (elem in toFeedList)
            serviceList.addService(elem)
    }

    private fun updateRecycler(recycler: RecyclerView, serviceList: ServiceDatasource) {
        serviceAdapter = ServiceItemAdapter(
            context as AreaActivity,
            serviceList.loadServiceInfo()
        ) { position -> onItemClick(position, serviceList) }
        recycler.setHasFixedSize(true)
        recycler.adapter = serviceAdapter
    }

    private fun onItemClick(position: Int, serviceList: ServiceDatasource) {
        val aboutClass = ((context as AreaActivity).application as AREAApplication).aboutClass ?: return
        val tokenTable = (((context as AreaActivity).application as AREAApplication).userInfo ?: return).tokensTable
        val serviceId = aboutClass.getServiceIdByName(serviceList.loadServiceInfo()[position].name) ?: return
        val oauthName = aboutClass.getServiceOAuthName(serviceId)
        if (oauthName == null || tokenTable[oauthName + "Token"] != null) {
            return ((context as AreaActivity).changeFragment(AreaCreationActionFragment(serviceList.loadServiceInfo()[position]), "action_creation"))
        }
        getOAuthLinkRequest(oauthName, position, context as AreaActivity)
    }

    private fun feelEntireList(aboutClass: AboutClass, servicesImages: List<Bitmap>): List<ServiceListElement> {
        val list = mutableListOf<ServiceListElement>()
        for (elem in aboutClass.getServiceList()) {
            if (elem.actions.isNotEmpty())
                list += ServiceListElement(elem.id, elem.name, servicesImages[elem.id - 1])
        }
        return list
    }

    private fun getOAuthLinkRequest(service: String, position: Int, context: Context) {
        val sessionManager = SessionManager(context as AreaActivity)
        val url = sessionManager.fetchAuthToken("url") ?: return
        val token = sessionManager.fetchAuthToken("user_token") ?:return
        val rep = Repository(url)
        val viewModelFactory = MainViewModelFactory(rep)
        val viewModel = ViewModelProvider(context, viewModelFactory)[MainViewModel::class.java]
        val observer: Observer<Response<String>?> = Observer { response ->
            if (response == null) {
                return@Observer
            }
            if (response.isSuccessful) {
                val oAuthLink = response.body()!!.toString()
                val bundle = Bundle()
                val intent = Intent(context as AreaActivity, OAuthLinkingActivity::class.java)
                bundle.putString("link", oAuthLink)
                bundle.putString("service", service)
                intent.putExtras(bundle)
                GlobalScope.launch {
                    waitForSuccess(position)
                }
                context.startActivity(intent)
            }
        }
        viewModel.getServiceLink(token, service, context, observer)
        viewModel.linkResponse.observe(context, observer)
    }

    private suspend fun waitForSuccess(position: Int) {
        while (((context as AreaActivity).application as AREAApplication).successOauth == null);
        if (((context as AreaActivity).application as AREAApplication).successOauth == true) {
            onSuccessOauth(context as AreaActivity, position)
        }
        else {
            onFailureOauth((context as AreaActivity))
        }
        ((context as AreaActivity).application as AREAApplication).successOauth = null;
    }

    private fun onSuccessOauth(context: Context, position: Int) {
        val fragment: AreaCreationActionServiceFragment = ((context as AreaActivity).supportFragmentManager.findFragmentByTag("action_service_creation")?: return) as AreaCreationActionServiceFragment

        while (fragment.isStateSaved);
        Handler(Looper.getMainLooper()).post {
            (context as AreaActivity).changeFragment(AreaCreationActionFragment(serviceList.loadServiceInfo()[position]), "action_creation")
            Toast.makeText(context as AreaActivity, "OAuth success!", Toast.LENGTH_LONG).show()
        }
    }

    private fun onFailureOauth(context: Context) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context as AreaActivity, "OAuth failure!", Toast.LENGTH_LONG).show()
        }
    }
}