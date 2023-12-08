package com.neta.uas_pppb

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.neta.uas_pppb.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var MovieUserAdapter: MovieUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager.getInstance(requireContext())

        MovieUserAdapter = MovieUserAdapter(requireContext(), ArrayList())

        MovieUserAdapter.setOnItemClickListener { selectedMovie ->
            val bundle = Bundle()
            bundle.putString("movieId", selectedMovie.id)
            val receiverFragment = DetailFragment()
            receiverFragment.arguments = bundle

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, receiverFragment)
            transaction.addToBackStack("ListFragment")
            transaction.commit()
        }

        with(binding){
            rvMovieUser.layoutManager = GridLayoutManager(requireContext(), 2)
            rvMovieUser.adapter = MovieUserAdapter
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
            MovieUserAdapter.setData(movies)
            MovieUserAdapter.notifyDataSetChanged()
        }
    }



}