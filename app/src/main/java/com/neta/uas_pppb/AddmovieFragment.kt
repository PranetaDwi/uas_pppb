package com.neta.uas_pppb

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.neta.uas_pppb.databinding.FragmentAddmovieBinding


class AddmovieFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private val ImageStorageRef = FirebaseStorage.getInstance().getReference("images")
    private var _binding: FragmentAddmovieBinding? = null
    private val binding get() = _binding!!
    private var imgPath: Uri? = null

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

                ImageStorageRef.putFile(imgPath!!)
                    .addOnSuccessListener {
                        ImageStorageRef.downloadUrl.addOnSuccessListener {
                            val imageFile = it.toString()
                            val newMovie = Movies(title = title, detail = detail, director = director, rate = rate, image = imageFile)
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
            imgPath = null
        }
    }

}