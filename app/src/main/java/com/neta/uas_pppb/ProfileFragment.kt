package com.neta.uas_pppb

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.neta.uas_pppb.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        prefManager = PrefManager.getInstance(requireContext())


        with(binding){
            userName1.text = prefManager.getUsername()
            logoutButton.setOnClickListener{
                prefManager.setLoggedIn(false)
                startActivity(Intent(requireContext(), MainActivity::class.java))
                prefManager.clear()
                requireActivity().finish()
            }
        }
        return binding.root
    }


}