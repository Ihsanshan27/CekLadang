package com.dicoding.cekladang.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.cekladang.data.local.entity.Plants
import com.dicoding.cekladang.databinding.FragmentHomeBinding
import com.dicoding.cekladang.ui.adapter.HomeAdapter
import com.dicoding.cekladang.ui.analisis.AnalisisActivity
import com.dicoding.cekladang.ui.result.ResultActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Menggunakan ViewModelProvider untuk mendapatkan ViewModel
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Menggunakan ViewBinding untuk layout fragment_home
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        binding.rvHome.layoutManager = LinearLayoutManager(requireContext())

        // Observe data plantList dari ViewModel
        homeViewModel.plantList.observe(viewLifecycleOwner) { plants ->
            // Update adapter ketika data berubah
            homeAdapter = HomeAdapter(plants) { selectedPlant ->
                // Logika ketika item tanaman diklik, panggil fungsi navigateToAnalisis
                navigateToAnalisis(selectedPlant)
            }
            binding.rvHome.adapter = homeAdapter
        }

        return binding.root
    }

    private fun navigateToAnalisis(selectedPlant: Plants) {
        // Membuat intent dan mengirimkan data modelPath
        val intent = Intent(requireContext(), AnalisisActivity::class.java)
        intent.putExtra("PLANT_NAME", selectedPlant.name)
        intent.putExtra("LABEL_NAME", selectedPlant.labelPath)
        intent.putExtra("MODEL_PATH", selectedPlant.modelPath) // Kirimkan modelPath untuk analisis
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tidak perlu null-kan binding jika menggunakan view binding.
    }
}