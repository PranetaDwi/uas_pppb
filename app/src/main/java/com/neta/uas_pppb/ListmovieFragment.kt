package com.neta.uas_pppb

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.neta.uas_pppb.databinding.FragmentListmovieBinding

class ListmovieFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private var _binding: FragmentListmovieBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var movieadminAdapter: MovieadminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListmovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager.getInstance(requireContext())

        movieadminAdapter = MovieadminAdapter(requireContext(), ArrayList())
        with(binding){
            logoutButton.setOnClickListener{
                prefManager.setLoggedIn(false)
                prefManager.clear()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }

            rvMovieAdmin.layoutManager = GridLayoutManager(requireContext(), 1)
            rvMovieAdmin.adapter = movieadminAdapter
            getMovies()

        }
    }

    fun getMovies(){
        MoviesCollectionRef.get().addOnSuccessListener {
                querySnapshot -> val movies = ArrayList<Movies>()

            for (document in querySnapshot){
                val movieData = document.toObject(Movies::class.java)
                movies.add(movieData)
            }
            movieadminAdapter.setData(movies)
            movieadminAdapter.notifyDataSetChanged()
        }
    }
}