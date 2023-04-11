package com.panda.admin.navbar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.panda.admin.MainActivity
import com.panda.admin.R


class ajustes : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_ajustes, container, true)
        var buttonLogout: Button = view.findViewById(R.id.btnLogout)
        buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Close the current activity
        }
        return view
    }
}