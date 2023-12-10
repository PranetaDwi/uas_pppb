package com.neta.uas_pppb.admin

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.neta.uas_pppb.R
import com.neta.uas_pppb.database.MoviesDao
import com.neta.uas_pppb.database.MoviesRoomDatabase
import com.neta.uas_pppb.database.Moviesdb
import com.neta.uas_pppb.firebase.Movies
import com.neta.uas_pppb.databinding.ActivityEditmovieBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EditmovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditmovieBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private val ImageStorageRef = FirebaseStorage.getInstance().getReference("images")
    private var imgPath: Uri? = null
    private lateinit var mMoviesDao: MoviesDao
    private var executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val channelId = "TEST_NOTIFICATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditmovieBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val db = MoviesRoomDatabase.getDatabase(this)
        mMoviesDao = db!!.moviesDao()!!

        val notification = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
                            Glide.with(this@EditmovieActivity)
                                .load(urlImage)
                                .into(picturePreview)
                        }
                    }
            }

            imageButton.setOnClickListener {
                val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(iImg, 0)
            }

            updateButton.setOnClickListener {
                val titleInput = titleInput.text.toString()
                val detailInput = detailInput.text.toString()
                val directorInput = directorInput.text.toString()
                val rateInput = rateInput.text.toString()

                if (imgPath != null) {
                    ImageStorageRef.putFile(imgPath!!)
                        .addOnSuccessListener {
                            ImageStorageRef.downloadUrl.addOnSuccessListener {
                                val imageFile = it.toString()
                                val editMovie = Movies(
                                    id = movieId,
                                    title = titleInput,
                                    detail = detailInput,
                                    director = directorInput,
                                    rate = rateInput,
                                    image = imageFile
                                )
                                executorService.execute {
                                    mMoviesDao.updateById(titleInput, detailInput, directorInput, rateInput, imgPath.toString(), movieId)
                                }
                                updateMovie(movieId, editMovie)

                            }
                        }
                } else {
                    val editMovie = Movies(id = movieId, title = titleInput, detail = detailInput, director = directorInput, rate = rateInput, image = urlImage)
                    executorService.execute {
                        mMoviesDao.updateByIdNoImage(titleInput, detailInput, directorInput, rateInput, movieId)
                    }
                    updateMovie(movieId, editMovie)
                }

                val notifImage = BitmapFactory.decodeResource(
                    resources, R.drawable.newnotif)

                val builder = NotificationCompat.Builder(this@EditmovieActivity, channelId)
                    .setSmallIcon(R.drawable.bellring)
                    .setContentTitle("New Movie Edited")
                    .setContentText("New movie has been added to the list")
                    .setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(notifImage)
                    )
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notifChannel = NotificationChannel(
                        channelId,
                        "Notifku",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    with(notification) {
                        createNotificationChannel(notifChannel)
                        notify(0, builder.build())
                    }
                }
                else {
                    notification.notify(0, builder.build())
                }
                setEmptyField()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imgPath = data?.data
        }

        Glide.with(this)
            .load(imgPath)
            .into(binding.picturePreview)
    }

    private fun updateMovie(movieId : String, movies : Movies){

        MoviesCollectionRef.document(movieId).set(movies)
            .addOnFailureListener {
                Log.d("UpdateActivity", "Error updating Movie: ", it)
            }
            .addOnSuccessListener {
                Log.d("UpdateActivity", "Movie successfully updated!")


                startActivity(Intent(this@EditmovieActivity, AdminActivity::class.java))

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