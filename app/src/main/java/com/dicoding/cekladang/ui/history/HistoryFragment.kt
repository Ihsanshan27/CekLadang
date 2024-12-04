package com.dicoding.cekladang.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.cekladang.databinding.FragmentHistoryBinding
import com.dicoding.cekladang.repository.HistoryRepository
import com.dicoding.cekladang.ui.ViewModelFactory
import com.dicoding.cekladang.ui.adapter.HistoryAdapter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val historyRepository = HistoryRepository(requireContext())
        val factory = ViewModelFactory.getInstance(historyRepository)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        adapter = HistoryAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)
        binding.rvHistory.adapter = adapter

        adapter.onClick = { history ->
            historyViewModel.delete(history)
        }

        historyViewModel.getAllHistoryUser().observe(viewLifecycleOwner, Observer { list ->
            if (list != null) {
                adapter.submitList(list)
                if (list.isEmpty()) {
                    binding.rvHistory.visibility = View.GONE
                } else {
                    binding.rvHistory.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}