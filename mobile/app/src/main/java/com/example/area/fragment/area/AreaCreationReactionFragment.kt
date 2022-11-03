package com.example.area.fragment.area

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.area.MainViewModel
import com.example.area.MainViewModelFactory
import com.example.area.R
import com.example.area.activity.AreaActivity
import com.example.area.adapter.ActionReactionItemAdapter
import com.example.area.adapter.ServiceItemAdapter
import com.example.area.data.ActionReactionDatasource
import com.example.area.data.ServiceDatasource
import com.example.area.model.AREAFields
import com.example.area.model.ActionReactionInfo
import com.example.area.model.ServiceInfo
import com.example.area.model.about.About
import com.example.area.model.about.AboutClass
import com.example.area.repository.Repository
import com.example.area.utils.AboutJsonCreator
import com.example.area.utils.SessionManager
import com.example.area.utils.actionReactionInfoTranslator
import com.google.android.material.textfield.TextInputEditText
import java.net.URL

class AreaCreationReactionFragment(private val actionService: ServiceInfo, private val action: ActionReactionInfo, private val reactionService: ServiceInfo) : Fragment(R.layout.fragment_area_creation_reaction) {
    private lateinit var viewModel: MainViewModel
    private var abt: About? = null
    private var reactionSelectedIndex = -1

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
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewReaction)
        val actionReactionList = ActionReactionDatasource()
        var temp: ActionReactionInfo?

        lateinit var injectedParams: ArrayAdapter<String>
        lateinit var abtCls: AboutClass
        val injectSpinner = view.findViewById<Spinner>(R.id.injectActionParamsSpinner)
        var last = 0

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
        val connection = URL(reactionService.imageUrl).openConnection()
        val inputStream = connection.getInputStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)

        view.findViewById<ImageView>(R.id.selectedReactionServiceLogoShow).setImageDrawable(
            BitmapDrawable(bitmap)
        )
        view.findViewById<TextView>(R.id.selectedReactionServiceTextShow).text = reactionService.name
        recycler.layoutManager = LinearLayoutManager(context as AreaActivity)
        updateRecycler(recycler, actionReactionList)
        view.findViewById<Button>(R.id.backFromReactionCreationButton).setOnClickListener {
            (context as AreaActivity).onBackPressed()
        }
        view.findViewById<Button>(R.id.areaRealCreationButton).setOnClickListener {
            if (reactionSelectedIndex != -1) {
                val textHint = view.findViewById<TextInputEditText>(R.id.reactionParamText).text
                if (textHint != null) {
                    if (textHint.isNotEmpty()) {
                        viewModel.areaCreation(
                            sessionManager.fetchAuthToken("user_token")!!,
                            AREAFields(actionService.id, action.id, action.paramName, reactionService.id, reactionSelectedIndex+1, textHint.toString())
                        )
                        viewModel.userResponse.observe(viewLifecycleOwner) { response ->
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context as AreaActivity,
                                    "Area added successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                (context as AreaActivity).changeFragment(AreaListFragment(), "area_list")
                            }
                        }
                    } else {
                        Toast.makeText(context as AreaActivity, "Please enter an reaction parameter", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context as AreaActivity, "Please select an reaction", Toast.LENGTH_SHORT).show()
            }
        }
        about.getAboutJson(context as AreaActivity, this, this) {
            abt = about.liveDataResponse.value
            if (abt != null) {
                abtCls = AboutClass(abt!!)
                actionReactionList.clear()
                for (elem in abt!!.server.services[reactionService.id-1].reactions) {
                    temp = actionReactionInfoTranslator(null, elem)
                    if (temp != null) {
                        actionReactionList.addService(temp!!.id, temp!!.name, temp!!.paramName, temp!!.description)
                    }
                }
                injectedParams = ArrayAdapter(context as AreaActivity, android.R.layout.simple_spinner_item, abtCls.getServiceActionAvailableInjectParamsById(actionService.id, action.id)!!)
                injectedParams.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                injectSpinner.isClickable = true
                injectSpinner.adapter = injectedParams
                injectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (p2 == last) {
                            injectSpinner.setSelection(0)
                            if (last != 0) {
                                if (p0 != null) {
                                    view.findViewById<TextInputEditText>(R.id.reactionParamText).text?.append("%${p0.selectedItem}%")
                                }
                                last = p2
                            }
                        } else {
                            if (p0 != null) {
                                view.findViewById<TextInputEditText>(R.id.reactionParamText).text?.append("%${p0.selectedItem}%")
                            }
                            last = p2
                        }
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
                updateRecycler(recycler, actionReactionList)
            }
        }
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        return view
    }

    private fun updateRecycler(recycler: RecyclerView, actionReactionList: ActionReactionDatasource) {
        recycler.setHasFixedSize(true)
        recycler.adapter = ActionReactionItemAdapter(
            context as AreaActivity,
            actionReactionList.loadActionReactionInfo()
        ) { position -> onItemClick(position, actionReactionList.loadActionReactionInfo()[position].name) }
    }

    private fun onItemClick(position: Int, toPrint: String) {
        reactionSelectedIndex = position
        Toast.makeText(context as AreaActivity, "$toPrint selected", Toast.LENGTH_SHORT).show()
    }
}