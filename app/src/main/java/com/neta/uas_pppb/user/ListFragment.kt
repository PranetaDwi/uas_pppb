package com.neta.uas_pppb.user

import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.neta.uas_pppb.adapter.MovieUserAdapter
import com.neta.uas_pppb.firebase.Movies
import com.neta.uas_pppb.PrefManager
import com.neta.uas_pppb.R
import com.neta.uas_pppb.adapter.MovieUserOfflineAdapter
import com.neta.uas_pppb.database.*
import com.neta.uas_pppb.databinding.FragmentListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ListFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val MoviesCollectionRef = firestore.collection("movies")
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var MovieUserAdapter: MovieUserAdapter
    private lateinit var MovieUserOfflineAdapter: MovieUserOfflineAdapter
    private lateinit var mMoviesDao: MoviesDao
    private lateinit var executorService: ExecutorService
    private lateinit var mCobaMoviesDao: CobaMoviesDao

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

        executorService = Executors.newSingleThreadExecutor()
        val dbbb = CobaMoviesDatabase.getDatabase(requireContext())
        mCobaMoviesDao = dbbb!!.cobaMoviesDao()!!

        MovieUserAdapter = MovieUserAdapter(requireContext(), ArrayList())
        MovieUserOfflineAdapter = MovieUserOfflineAdapter(requireContext(), ArrayList())

        MovieUserAdapter.setOnItemClickListener { selectedMovie ->
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
            transaction.addToBackStack("ListFragment")
            transaction.commit()
        }

        MovieUserOfflineAdapter.setOnItemClickListener { selectedMovie ->
            val bundle = Bundle()
            bundle.putString("movieId", selectedMovie.id)
            bundle.putString("titles", selectedMovie.title)
            bundle.putString("detail", selectedMovie.detail)
            bundle.putString("director", selectedMovie.director)
            bundle.putString("rating", selectedMovie.rate)
            bundle.putString("duration", selectedMovie.duration)
            bundle.putString("genre", selectedMovie.genre)
            bundle.putString("image", "null")
            val receiverFragment = DetailFragment()
            receiverFragment.arguments = bundle

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, receiverFragment)
            transaction.addToBackStack("ListFragment")
            transaction.commit()
        }

        with(binding){
            rvMovieUser.layoutManager = GridLayoutManager(requireContext(), 2)
            if (checkInternetConnection(requireContext())){
                rvMovieUser.adapter = MovieUserAdapter
                fetchDataFromFirestoreAndSaveToLocal()
                getMovies()
            } else {
                rvMovieUser.adapter = MovieUserOfflineAdapter
                getMoviesOffline()
            }

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

    fun getMoviesOffline() {
        mCobaMoviesDao.allMovies.observe(viewLifecycleOwner) { movie ->
            MovieUserOfflineAdapter.setData(movie)
            MovieUserOfflineAdapter.notifyDataSetChanged()
        }
        executorService.shutdown()
    }

    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION") return networkInfo.isConnected
        }
    }

    // coba pake punya klin
    private fun fetchDataFromFirestoreAndSaveToLocal(){
        Log.d("FirebaseToLocal", "Mulai penyalinan data dari Firestore ke lokal")

        MoviesCollectionRef.get().addOnSuccessListener { documents ->
            val movieModels = mutableListOf<Movies>()
            for (document in documents){
                val movie = document.toObject<Movies>()
                movieModels.add(movie)
            }
            val movieEntities = convertToMovieEntity(movieModels)
            CoroutineScope(Dispatchers.IO).launch {
                mCobaMoviesDao.insert(movieEntities)
                withContext(Dispatchers.Main){
                    Log.d("FirebaseToLocal", "Sukses menyalin data")
                }
            }
        }.addOnFailureListener{ exception ->
            Log.e("FireStore", "Error getting documents")
        }


    }

    private fun convertToMovieEntity(movieModels: List<Movies>): List<CobaMovies>{
        val movieEntities = mutableListOf<CobaMovies>()
        for (movieModel in movieModels){
            val moviedb = CobaMovies(
                movieModel.id,
                movieModel.title,
                movieModel.detail,
                movieModel.director,
                movieModel.rate,
                movieModel.duration,
                movieModel.genre,
                movieModel.image
            )
            movieEntities.add(moviedb)
        }
        return movieEntities
    }

}