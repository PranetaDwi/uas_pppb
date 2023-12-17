package com.neta.uas_pppb.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.neta.uas_pppb.firebase.Favorites
import com.neta.uas_pppb.PrefManager
import com.neta.uas_pppb.R
import com.neta.uas_pppb.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private val FavoritesCollectionRef = firestore.collection("favorites")
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = arguments?.getString("movieId").toString()
        prefManager = PrefManager.getInstance(requireContext())
        val userId = prefManager.getUserId()

        with(binding){

            val image = arguments?.getString("image")
            detailTitle.setText(arguments?.getString("titles"))
            detailDescription.setText(arguments?.getString("detail"))
            detailDirector.setText(arguments?.getString("director"))
            detailRating.setText(arguments?.getString("rating"))
            if (image == "null"){
                Glide.with(this@DetailFragment)
                .load(R.drawable.loading_buffering)
                .into(moviePicture)
                Glide.with(this@DetailFragment)
                    .load(image)
                    .into(txtBigPicture)
            } else {
                Glide.with(this@DetailFragment)
                .load(image)
                .into(moviePicture)
                Glide.with(this@DetailFragment)
                    .load(image)
                    .into(txtBigPicture)
            }

            favoritButton.setOnClickListener {
                val newFavorit = Favorites(user_id = userId, movie_id = movieId)
                addFavorit(newFavorit)
            }
        }

    }

    private fun addFavorit(favorit: Favorites){
        FavoritesCollectionRef.add(favorit)
            .addOnSuccessListener { docRef ->
                val createFavoritId = docRef.id
                favorit.id = createFavoritId
                docRef.set(favorit)
                    .addOnFailureListener{
                        Log.d("RegisterActivity", "Error Updating User: ", it)
                    }
                Toast.makeText(requireContext(), "Berhasil Menambahkan ke Favorit", Toast.LENGTH_SHORT).show()
            }
            . addOnFailureListener{
                Log.d("FormActivity", "Error adding budget ID: ", it)
            }
    }

}