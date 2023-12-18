package com.neta.uas_pppb.admin

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.neta.uas_pppb.R
import com.neta.uas_pppb.database.MoviesDao
import com.neta.uas_pppb.database.MoviesRoomDatabase
import com.neta.uas_pppb.database.Moviesdb
import com.neta.uas_pppb.firebase.Movies
import com.neta.uas_pppb.databinding.FragmentAddmovieBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AddmovieFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private val ImageStorageRef = FirebaseStorage.getInstance().getReference("images")
    private var _binding: FragmentAddmovieBinding? = null
    private val binding get() = _binding!!
    private var imgPath: Uri? = null
    private val channelId = "TEST_NOTIFICATION"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddmovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            imageButton.setOnClickListener {
                val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(iImg, 0)
            }

            addButton.setOnClickListener{
                val title = titleInput.text.toString()
                val detail = detailInput.text.toString()
                val director = directorInput.text.toString()
                val rate = rateInput.text.toString()
                val duration = durationInput.text.toString()
                val genre = genreInput.text.toString()

                ImageStorageRef.putFile(imgPath!!)
                    .addOnSuccessListener {
                        ImageStorageRef.downloadUrl.addOnSuccessListener {
                            val imageFile = it.toString()
                            val newMovie = Movies(title = title, detail = detail, director = director, rate = rate, duration = duration, genre = genre, image = imageFile)
                            addMovie(newMovie)
                            startActivity(Intent(requireContext(), AdminActivity::class.java))
                        }
                    }

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

    private fun addMovie(movie: Movies){
        MoviesCollectionRef.add(movie)
            .addOnSuccessListener { docRef ->
                val createMovieId = docRef.id
                movie.id = createMovieId
                docRef.set(movie)
                    .addOnFailureListener{
                        Log.d("RegisterActivity", "Error Updating User: ", it)
                    }

                val notification = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notifImage = BitmapFactory.decodeResource(
                    resources, R.drawable.newnotif)

                val builder = NotificationCompat.Builder(requireContext(), channelId)
                    .setSmallIcon(R.drawable.bellring)
                    .setContentTitle("New Movie Added")
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
                resetForm()
            }
            . addOnFailureListener{
                Log.d("FormActivity", "Error adding budget ID: ", it)
            }
    }

    private fun resetForm(){
        with(binding){
            titleInput.setText("")
            detailInput.setText("")
            directorInput.setText("")
            rateInput.setText("")
            durationInput.setText("")
            genreInput.setText("")
            imgPath = null
        }
    }

}