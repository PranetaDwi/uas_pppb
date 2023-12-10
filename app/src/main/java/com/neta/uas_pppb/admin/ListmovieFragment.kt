package com.neta.uas_pppb.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.neta.uas_pppb.auth.MainActivity
import com.neta.uas_pppb.adapter.MovieadminAdapter
import com.neta.uas_pppb.firebase.Movies
import com.neta.uas_pppb.PrefManager
import com.neta.uas_pppb.databinding.FragmentListmovieBinding

class ListmovieFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private var _binding: FragmentListmovieBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var movieadminAdapter: MovieadminAdapter

    companion object {
        const val MOVIE_ID = "movie_id"
        const val MOVIE_TITLE = "movie_title"
        const val MOVIE_IMAGE = "movie_image"
        const val MOVIE_DETAIL = "movie_detail"
        const val MOVIE_DIRECTOR = "movie_director"
        const val MOVIE_RATE = "movie_rate"
    }

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

        movieadminAdapter.setOnItemClickListener { selectedMovie ->
            val intentToDetailmovieActivity = Intent(requireContext(), DetailmovieActivity::class.java)
            intentToDetailmovieActivity.putExtra(MOVIE_ID, selectedMovie.id)
            intentToDetailmovieActivity.putExtra(MOVIE_TITLE, selectedMovie.title)
            intentToDetailmovieActivity.putExtra(MOVIE_IMAGE, selectedMovie.image)
            intentToDetailmovieActivity.putExtra(MOVIE_DETAIL, selectedMovie.detail)
            intentToDetailmovieActivity.putExtra(MOVIE_DIRECTOR, selectedMovie.director)
            intentToDetailmovieActivity.putExtra(MOVIE_RATE, selectedMovie.rate)
            startActivity(intentToDetailmovieActivity)
        }

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