package com.remziakgoz.artbookkotlinhomework.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.remziakgoz.artbookkotlinhomework.MainActivity
import com.remziakgoz.artbookkotlinhomework.adapter.ArtAdapter
import com.remziakgoz.artbookkotlinhomework.databinding.FragmentHomeBinding
import com.remziakgoz.artbookkotlinhomework.model.Art
import com.remziakgoz.artbookkotlinhomework.roomdb.ArtDao
import com.remziakgoz.artbookkotlinhomework.roomdb.ArtDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val compositeDisposable = CompositeDisposable()
    private lateinit var db: ArtDatabase
    private lateinit var navController: NavController

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = Room.databaseBuilder(requireContext(),ArtDatabase::class.java, "Arts").build()

        navController = findNavController()

//        val db = (activity as MainActivity).db

        val artDao = db.artDao()

        compositeDisposable.add(
            artDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

        return root
    }

    private fun handleResponse(artList : List<Art>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ArtAdapter(artList, navController)
        binding.recyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}