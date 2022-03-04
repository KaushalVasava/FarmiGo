package com.lahsuak.apps.farmigo.ui.fragments


import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lahsuak.apps.farmigo.adapter.CityAdapter
import com.lahsuak.apps.farmigo.databinding.FragmentCitiesBinding
import com.lahsuak.apps.farmigo.model.Farm
import java.util.*
import com.lahsuak.apps.farmigo.R
import java.io.IOException
import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*


class CityFragment : Fragment(R.layout.fragment_cities) {
    private var farmList = mutableListOf<Farm>()
    private lateinit var binding: FragmentCitiesBinding
    private lateinit var adapter: CityAdapter

    private lateinit var selectedUserLocation: String

    private val args: CityFragmentArgs by navArgs()

    private val backPressedDispatcher = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Redirect to our own function
            this@CityFragment.onBackPressed()
        }
    }

    inner class FarmComparator : Comparator<Farm> {
        override fun compare(m1: Farm, m2: Farm): Int {
            val result = m1.distance.compareTo(m2.distance)
            if (result > 0)
                return 1
            else if (result < 0)
                return -1
            else
                return 0
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).setSupportActionBar(view?.findViewById(R.id.toolbar))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCitiesBinding.bind(view)
        binding.selectedLocation.text = args.locationName
        selectedUserLocation = args.cityName
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show() // to show this dialog

        val list = mutableListOf<Farm>()
        list.addAll(sortSongs(farmList as ArrayList<Farm>))
        adapter = CityAdapter(list)
        binding.apply {
            rvCities.setHasFixedSize(true)
            rvCities.adapter = adapter
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        this.viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                getData()
            }
            withContext(Dispatchers.Main) {
                adapter = CityAdapter(farmList)
                binding.rvCities.adapter = adapter
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }


        // Redirect system "Back" press to our dispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedDispatcher
        )

    }

    private fun getData() {
        farmList.add(Farm("Pune", "Maharashtra", computeDistanceBetween("pune")))
        farmList.add(Farm("Mumbai", "Maharashtra", computeDistanceBetween("mumbai")))
        farmList.add(Farm("Indore", "Madya Pradesh", computeDistanceBetween("indore")))
        farmList.add(
            Farm(
                "Bangalore",
                "Karnataka", computeDistanceBetween("bangalore")
            )
        )
        farmList.add(Farm("Ahmedabad", "Gujarat", computeDistanceBetween("ahmedabad")))
        farmList.add(Farm("Chennai", "Tamil nadu", computeDistanceBetween("chennai")))
        farmList.add(
            Farm(
                "Kolkata",
                "West Bengal", computeDistanceBetween("kolkata")
            )
        )
        farmList.add(Farm("Delhi", "Delhi", computeDistanceBetween("delhi")))

    }

    private fun getLocationPoint(locationAddress: String): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var address: Address? = null
        try {
            val addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size > 0) {
                address = addressList[0] as Address
            }
        } catch (e: IOException) {
        }
        return address
    }

    private fun sortSongs(list: ArrayList<Farm>): ArrayList<Farm> {
        Collections.sort(list, FarmComparator())
        return list
    }

    private fun onBackPressed() {
        findNavController().popBackStack()
    }

    private fun computeDistanceBetween(locationName: String): Float {
        val floatAr = FloatArray(1)

        val loc1 = getLocationPoint(selectedUserLocation)
        val loc2 = getLocationPoint(locationName)
        if (loc1 != null && loc2 != null) {
            Location.distanceBetween(
                loc1.latitude,
                loc1.longitude,
                loc2.latitude,
                loc2.longitude,
                floatAr
            )
        }
        var distance = 0f
        distance += floatAr[0]
        return distance
    }
}