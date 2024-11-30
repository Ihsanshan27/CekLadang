package com.dicoding.cekladang.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.cekladang.databinding.FragmentHomeBinding
import com.dicoding.cekladang.ui.adapter.HomeAdapter
import com.dicoding.cekladang.ui.analisis.AnalisisActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.rvHome
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeViewModel.home.observe(viewLifecycleOwner) { homeList ->
            Log.d("Home Fragment", "Data: $homeList")
            val adapter = HomeAdapter(homeList) { selectedItem ->
                navigateToAnalisis(selectedItem)

            }
            recyclerView.adapter = adapter
        }

        return root
    }

    private fun navigateToAnalisis(selectedItem: String) {
        val intent = Intent(requireContext(), AnalisisActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}