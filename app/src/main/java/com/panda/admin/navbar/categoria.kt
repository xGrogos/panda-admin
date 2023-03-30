package com.panda.admin.navbar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.panda.admin.R
import com.panda.admin.adapters.RecyclerCategoriaAdapter
import com.panda.admin.firebase.pieza


class categoria : Fragment() {
    lateinit var llistat: ArrayList<pieza>;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v:View = inflater.inflate(R.layout.fragment_categoria, container, false)
        // Referència de la bbdd
        val database = Firebase.database

        llistat = ArrayList<pieza>();

        database.getReference("Piezas").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot:DataSnapshot in snapshot.getChildren()) {
                    val pieza: pieza? = snapshot.getValue<pieza>();
                    if (pieza != null) {
                        llistat.add(pieza)
                    };
                    cargarView(v)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseTest", "Failed to read value.", error.toException())
            }
        })
        return v;
    }
    fun cargarView(v:View){
        var recyclerView: RecyclerView = v.findViewById(R.id.recyclerLlistat);
        recyclerView.layoutManager = GridLayoutManager(context,2, GridLayoutManager.VERTICAL, false)
        val adapter = RecyclerCategoriaAdapter(llistat, context);
        recyclerView.adapter = adapter
    }
}