package com.neta.uas_pppb

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.neta.uas_pppb.databinding.ActivityEditmovieBinding

class EditmovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditmovieBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private val ImageStorageRef = FirebaseStorage.getInstance().getReference("images")
    private var imgPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditmovieBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            val movieId = intent.getStringExtra(DetailmovieActivity.MOVIE_ID).toString()
            val urlImage = intent.getStringExtra(DetailmovieActivity.MOVIE_IMAGE).toString()

            if (!movieId.isNullOrBlank()) {
                val movieDocumentRef = MoviesCollectionRef.document(movieId)
                movieDocumentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            titleInput.setText(documentSnapshot.getString("title"))
                            detailInput.setText(documentSnapshot.getString("detail"))
                            directorInput.setText(documentSnapshot.getString("director"))
                            rateInput.setText(documentSnapshot.getString("rate"))
                        }
                    }
            }

            imageButton.setOnClickListener {
                val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(iImg, 0)
            }

            updateButton.setOnClickListener {
                val title = titleInput.text.toString()
                val detail = detailInput.text.toString()
                val director = directorInput.text.toString()
                val rate = rateInput.text.toString()

                if (imgPath != null) {
                    ImageStorageRef.putFile(imgPath!!)
                        .addOnSuccessListener {
                            ImageStorageRef.downloadUrl.addOnSuccessListener {
                                val imageFile = it.toString()
                                val editMovie = Movies(
                                    id = movieId,
                                    title = title,
                                    detail = detail,
                                    director = director,
                                    rate = rate,
                                    image = imageFile
                                )
                                updateMovie(movieId, editMovie)
                            }
                        }
                } else {
                    val editMovie = Movies(id = movieId, title = title, detail = detail, director = director, rate = rate, image = urlImage)
                    updateMovie(movieId, editMovie)
                }
                setEmptyField()
                startActivity(Intent(this@EditmovieActivity, AdminActivity::class.java))
            }
        }
    }

    private fun updateMovie(movieId : String, movies : Movies){
        MoviesCollectionRef.document(movieId).set(movies)
            .addOnFailureListener {
                Log.d("UpdateActivity", "Error updating Movie: ", it)
            }
    }

    private fun setEmptyField() {
        with(binding) {
            titleInput.setText("")
            detailInput.setText("")
            directorInput.setText("")
            rateInput.setText("")
        }
    }


}