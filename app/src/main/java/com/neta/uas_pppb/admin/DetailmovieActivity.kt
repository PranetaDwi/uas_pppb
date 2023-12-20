package com.neta.uas_pppb.admin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.neta.uas_pppb.R
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

        supportActionBar?.hide()

        val movieId = intent.getStringExtra(ListmovieFragment.MOVIE_ID).toString()
        val urlImage = intent.getStringExtra(ListmovieFragment.MOVIE_IMAGE).toString()

        val positiveButtonClick = {
            dialog: DialogInterface, which: Int -> deleteMovie(movieId, urlImage)
            dialog.dismiss()
        }

        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        with(binding){
            detailTitle.text = intent.getStringExtra(ListmovieFragment.MOVIE_TITLE)
            detailDescription.text = intent.getStringExtra(ListmovieFragment.MOVIE_DETAIL)
            detailDirector.text = intent.getStringExtra(ListmovieFragment.MOVIE_DIRECTOR)
            detailRating.text = intent.getStringExtra(ListmovieFragment.MOVIE_RATE)
            detailDuration.text = intent.getStringExtra(ListmovieFragment.MOVIE_DURATION)
            detailGenre.text = intent.getStringExtra(ListmovieFragment.MOVIE_GENRE)
            Glide.with(this@DetailmovieActivity)
                .load(intent.getStringExtra(ListmovieFragment.MOVIE_IMAGE))
                .into(moviePicture)


            deleteButton.setOnClickListener{

                val builder = AlertDialog.Builder(this@DetailmovieActivity)

                with(builder){
                    setTitle("Delete Confirmation")
                    setMessage("Are You Sure to Delete this Movie?")
                    setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
                    setNegativeButton(android.R.string.no, negativeButtonClick)
                    show()
                }


            }

            editButton.setOnClickListener{
                val intentToEditmovieActivity = Intent(this@DetailmovieActivity, EditmovieActivity::class.java)
                intentToEditmovieActivity.putExtra(MOVIE_ID, movieId)
                intentToEditmovieActivity.putExtra(MOVIE_IMAGE, urlImage)
                startActivity(intentToEditmovieActivity)
            }

            backButton.setOnClickListener{
                startActivity(Intent(this@DetailmovieActivity, AdminActivity::class.java))
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
            .addOnSuccessListener {
                Log.d("DetailActivity", "Budget successfully deleted!")
                Toast.makeText(this@DetailmovieActivity, "Berhasil Menghapus Movie", Toast.LENGTH_SHORT).show()

            val imageStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(UrlImage)

            imageStorageRef.delete()
                .addOnFailureListener{
                    Log.d("DetailActivity", "Error deleting budget: ", it)
                }

                startActivity(Intent(this@DetailmovieActivity, AdminActivity::class.java))
        }
    }
}