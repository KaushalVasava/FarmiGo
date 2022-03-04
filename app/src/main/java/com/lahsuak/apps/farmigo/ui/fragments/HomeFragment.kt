package com.lahsuak.apps.farmigo.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lahsuak.apps.farmigo.R
import com.lahsuak.apps.farmigo.databinding.FragmentHomeBinding
import com.lahsuak.apps.farmigo.model.Data
import com.lahsuak.apps.farmigo.ui.viewmodel.FarmViewModel
import com.lahsuak.apps.farmigo.util.Constants.DATA_FARMIGO
import com.lahsuak.apps.farmigo.util.Constants.FIRST_TIME
import com.lahsuak.apps.farmigo.util.HomeUtility.notifyUser


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: FarmViewModel
    private lateinit var stateAdapter: ArrayAdapter<*>
    private lateinit var districtAdapter: ArrayAdapter<*>
    private lateinit var talukaAdapter: ArrayAdapter<*>
    private lateinit var villageAdapter: ArrayAdapter<*>

    private val stateList = mutableListOf<String>()
    private val districtList = mutableListOf<String>()
    private val talukaList = mutableListOf<String>()
    private val villageList = mutableListOf<String>()

    companion object {
        var order1 = 0
        var order2 = 0
        var order3 = 0
        var order4 = 0
    }

    private var firstTime = false
    private lateinit var selectedLocation: String
    private lateinit var selectedCityName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        viewModel = ViewModelProvider(this).get(FarmViewModel::class.java)

        val pref = requireContext().getSharedPreferences(DATA_FARMIGO, MODE_PRIVATE)
        firstTime = pref.getBoolean(FIRST_TIME, false)

        initAdapter()
        if (!firstTime) {
            retrieveDataFromApi()
            if (stateList.size != 0)
                selectedLocation = stateList[order4]
            if (districtList.size != 0)
                selectedCityName = districtList[order2]
        } else {
            retrieveDataFromFirebase()

            if (stateList.size != 0) {
                selectedLocation = stateList[order4]
            }
            if (districtList.size != 0) {
                selectedCityName = districtList[order2]
            }
        }

        binding.apply {
            btnComplete.setOnClickListener {
                if (validation()) {
                    val action = HomeFragmentDirections.actionHomeFragmentToCityFragment(
                        selectedCityName, selectedLocation
                    )
                    findNavController().navigate(action)
                }
            }

            if (radioGroup.checkedRadioButtonId == R.id.farmer) {
                farmer.isChecked = true
                trader.isChecked = false
            } else {
                trader.isChecked = true
                farmer.isChecked = false
            }
            spState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (order1 != position) {
                        order1 = position
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
            spDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (order2 != position) {
                        order2 = position
                        selectedCityName = districtList[position]
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
            spTaluka.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (order3 != position) {
                            order3 = position
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }
            spVillage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (order4 != position) {
                        order4 = position
                        selectedLocation = villageList[position]
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun initAdapter() {
        stateAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, stateList)
        districtAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, districtList)
        talukaAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, talukaList)
        villageAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, villageList)

        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        talukaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private fun validation(): Boolean {
        val name = binding.txtName.text.toString()
        val phNo = binding.txtPhNumber.text.toString()
        return when {
            TextUtils.isEmpty(name) -> {
                notifyUser(requireContext(), "Please enter name")
                false
            }
            TextUtils.isEmpty(phNo) || (phNo.length < 10) -> {
                notifyUser(requireContext(), "Please enter valid phone number")
                false
            }
            else -> true
        }
    }

    private fun retrieveDataFromApi() {
        viewModel.getMarket().observe(this, { data ->
            for ((i, item) in data.records.withIndex()) {
                stateList.add(item.state_name)
                districtList.add(item.district_name)
                talukaList.add(item.sub_district_name)
                villageList.add(item.area_name)
                saveData(
                    i,
                    item.state_name,
                    item.district_name,
                    item.sub_district_name,
                    item.area_name
                )
            }
            binding.spState.adapter = stateAdapter
            binding.spDistrict.adapter = districtAdapter
            binding.spTaluka.adapter = talukaAdapter
            binding.spVillage.adapter = villageAdapter

            binding.spState.setSelection(order1)
            binding.spDistrict.setSelection(order2)
            binding.spTaluka.setSelection(order3)
            binding.spVillage.setSelection(order4)

        })
    }

    private fun retrieveDataFromFirebase() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Data")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stateList.clear()
                districtList.clear()
                talukaList.clear()
                villageList.clear()
                for (sp in snapshot.children) {
                    val user = sp.getValue(Data::class.java)
                    if (user != null) {
                        stateList.add(user.state_name)
                        districtList.add(user.district_name)
                        talukaList.add(user.sub_district_name)
                        villageList.add(user.area_name)
                    }
                }
                binding.spState.adapter = stateAdapter
                binding.spDistrict.adapter = districtAdapter
                binding.spTaluka.adapter = talukaAdapter
                binding.spVillage.adapter = villageAdapter

                binding.spState.setSelection(order1)
                binding.spDistrict.setSelection(order2)
                binding.spTaluka.setSelection(order3)
                binding.spVillage.setSelection(order4)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun saveData(
        counter: Int,
        state: String,
        district: String,
        taluka: String,
        village: String
    ) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Data")
        val userMap = HashMap<String, Any>()
        userMap["state_name"] = state
        userMap["district_name"] = district
        userMap["sub_district_name"] = taluka
        userMap["area_name"] = village

        userRef.child(counter.toString()).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val pref =
                        requireContext().getSharedPreferences(DATA_FARMIGO, MODE_PRIVATE).edit()
                    pref.putBoolean(FIRST_TIME, true)
                    pref.apply()
                } else {
                    notifyUser(requireContext(), "Something went wrong!")
                }
            }
    }

}
