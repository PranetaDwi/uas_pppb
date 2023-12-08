package com.neta.uas_pppb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.neta.uas_pppb.databinding.ActivityDetailmovieBinding

class DetailmovieActivity : AppCompatActivity() {

    companion object {
        const val MOVIE_ID = "movie_id"
        const val MOVIE_IMAGE = "movie_image"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private lateinit var binding: ActivityDetailmovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailmovieBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            val movieId = intent.getStringExtra(ListmovieFragment.MOVIE_ID).toString()
            val urlImage = intent.getStringExtra(ListmovieFragment.MOVIE_IMAGE).toString()
            detailTitle.text = intent.getStringExtra(ListmovieFragment.MOVIE_TITLE)
            detailDescription.text = intent.getStringExtra(ListmovieFragment.MOVIE_DETAIL)
            detailDirector.text = intent.getStringExtra(ListmovieFragment.MOVIE_DIRECTOR)
            detailRating.text = intent.getStringExtra(ListmovieFragment.MOVIE_RATE)
            Glide.with(this@DetailmovieActivity)
                .load(intent.getStringExtra(ListmovieFragment.MOVIE_IMAGE))
                .into(moviePicture)


            deleteButton.setOnClickListener{
                deleteMovie(movieId, urlImage)
                startActivity(Intent(this@DetailmovieActivity, AdminActivity::class.java))
            }

            editButton.setOnClickListener{
                val intentToEditmovieActivity = Intent(this@DetailmovieActivity, EditmovieActivity::class.java)
                intentToEditmovieActivity.putExtra(MOVIE_ID, movieId)
                intentToEditmovieActivity.putExtra(MOVIE_IMAGE, urlImage)
                startActivity(intentToEditmovieActivity)
            }
        }
    }

    private fun deleteMovie(MovieId : String, UrlImage : String){
        if (MovieId.isEmpty()){
            Log.d("DetailActivity", "Error deleting: budget ID is empty!")
            return
        }

        MoviesCollectionRef.document(MovieId).delete()
            .addOnFailureListener{
                Log.d("DetailActivity", "Error deleting budget: ", it)
            }

        val imageStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(UrlImage)

        imageStorageRef.delete()
            .addOnFailureListener{
                Log.d("DetailActivity", "Error deleting budget: ", it)
            }
    }
}