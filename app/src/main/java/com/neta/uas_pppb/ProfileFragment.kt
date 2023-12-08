package com.neta.uas_pppb

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.neta.uas_pppb.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollectionRef = firestore.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager.getInstance(requireContext())

//        val userId = arguments?.getString("userId").toString()

        with(binding){
            val userId = prefManager.getUserId()

            if (!userId.isNullOrBlank()) {
                val userDocumentRef = usersCollectionRef.document(userId)
                userDocumentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            userName1.setText(documentSnapshot.getString("name"))
                            userName2.setText(documentSnapshot.getString("name"))
                            userEmail.setText(documentSnapshot.getString("email"))
                            userPhone.setText(documentSnapshot.getString("phone"))
                        }
                    }
            }

            logoutButton.setOnClickListener{
                prefManager.setLoggedIn(false)
                startActivity(Intent(requireContext(), MainActivity::class.java))
                prefManager.clear()
                requireActivity().finish()
            }
        }
    }


}