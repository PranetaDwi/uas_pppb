package com.neta.uas_pppb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.neta.uas_pppb.databinding.FragmentFavoritBinding
import com.neta.uas_pppb.databinding.FragmentProfileBinding

class FavoritFragment : Fragment() {

    private lateinit var prefManager: PrefManager
    private var _binding: FragmentFavoritBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private val FavoritesCollectionRef = firestore.collection("favorites")
    private lateinit var FavoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentFavoritBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager.getInstance(requireContext())
        val userId = prefManager.getUserId()

        FavoritesAdapter = FavoritesAdapter(requireContext(), ArrayList())

        val favoritesQuery = FavoritesCollectionRef.whereEqualTo("user_id", userId)


         with(binding){
             rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
             rvFavorites.adapter = FavoritesAdapter

             favoritesQuery.get().addOnSuccessListener { favoritesSnapshot ->
                 val favoriteList = favoritesSnapshot.toObjects(Favorites::class.java)
                 val movieIds = favoriteList.map { it.movie_id }

                 if (movieIds.isNotEmpty()){
                     fetchMoviesByMovieIds(movieIds)
                 }
             }.addOnFailureListener { e ->
                 Log.e("FavoritFragment", "Error fetching favorites: $e")
             }
         }
    }

     private fun fetchMoviesByMovieIds(movieIds: List<String>){
        val movieQuery = MoviesCollectionRef.whereIn("id", movieIds)
        movieQuery.get().addOnSuccessListener { moviesSnapshot ->
            val moviesList = moviesSnapshot.toObjects(Movies::class.java)

            FavoritesAdapter.setData(moviesList)
            FavoritesAdapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("FavoritFragment", "Error fetching movies: $e")
        }
    }


}