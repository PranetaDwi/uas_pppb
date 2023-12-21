package com.neta.uas_pppb.user

import android.content.Context
import android.content.Intent
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.neta.uas_pppb.firebase.Favorites
import com.neta.uas_pppb.adapter.FavoritesAdapter
import com.neta.uas_pppb.firebase.Movies
import com.neta.uas_pppb.PrefManager
import com.neta.uas_pppb.R
import com.neta.uas_pppb.admin.AdminActivity
import com.neta.uas_pppb.databinding.FragmentFavoritBinding

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

        getDataFavorit()

        FavoritesAdapter = FavoritesAdapter(requireContext(), ArrayList(),
                onClickMovie = { clickedMovie ->
        },
            onClickMovie2 = { clickedMovie ->
            }
        )

        FavoritesAdapter.onClickMovie = {
                clickedFavorite -> deleteMovie(clickedFavorite.id)
        }

         with(binding){
             rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
             rvFavorites.adapter = FavoritesAdapter

         }

        FavoritesAdapter.onClickMovie2 = {
         selectedMovie ->
                val bundle = Bundle()
                bundle.putString("movieId", selectedMovie.id)
                bundle.putString("titles", selectedMovie.title)
                bundle.putString("detail", selectedMovie.detail)
                bundle.putString("director", selectedMovie.director)
                bundle.putString("rating", selectedMovie.rate)
                bundle.putString("duration", selectedMovie.duration)
                bundle.putString("genre", selectedMovie.genre)
                bundle.putString("image", selectedMovie.image)
                val receiverFragment = DetailFragment()
                receiverFragment.arguments = bundle

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, receiverFragment)
                transaction.addToBackStack("FavoritFragment")
                transaction.commit()
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

    private fun deleteMovie(FavoritId : String){
        if (FavoritId.isEmpty()){
            Log.d("DetailActivity", "Error deleting: budget ID is empty!")
            return
        }

        val query = FavoritesCollectionRef.whereEqualTo("movie_id", FavoritId)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                document.reference.delete()
                    .addOnSuccessListener {
                        Log.d("FavoritFragment", "Favorite successfully deleted!")
                        Toast.makeText(requireContext(), "Berhasil Menghapus Favorit", Toast.LENGTH_SHORT).show()
                        getDataFavorit()
                        FavoritesAdapter.notifyDataSetChanged()


                    }
                    .addOnFailureListener { e ->
                        Log.e("FavoritFragment", "Error deleting favorite: $e")
                    }
            } else {
                Log.d("FavoritFragment", "Favorite not found with movie_id: $FavoritId")
            }
        }.addOnFailureListener { e ->
            Log.e("FavoritFragment", "Error querying favorites: $e")
        }
    }

    private fun getDataFavorit(){
        val userId = prefManager.getUserId()
        val favoritesQuery = FavoritesCollectionRef.whereEqualTo("user_id", userId)
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