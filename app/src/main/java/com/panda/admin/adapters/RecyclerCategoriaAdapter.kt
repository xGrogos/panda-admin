package com.panda.admin.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.panda.admin.R
import com.panda.admin.firebase.pieza


class RecyclerCategoriaAdapter(llistat: MutableList<pieza>, context: Context?): RecyclerView.Adapter<RecyclerCategoriaAdapter.ViewHolder>() {
    var llistat: MutableList<pieza> = llistat;
    var context: Context? = context;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return ViewHolder(layoutInflater.inflate(R.layout.lista_pieza, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtNombre.setText(llistat.get(position).nombre);
        holder.txtColor.setText(llistat.get(position).color);
        holder.txtPrecio.setText(llistat.get(position).precio+" €");
        holder.txtMarca.setText(llistat.get(position).marca);
        Glide.with(context!!)
            .load(llistat.get(position).imageURL)
            .into(holder.imageView)

    }


    override fun getItemCount(): Int {
        return llistat.size;
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.nombrePieza)
        val txtColor: TextView = view.findViewById(R.id.colorPieza)
        val txtPrecio: TextView = view.findViewById(R.id.precioPieza)
        val txtMarca: TextView = view.findViewById(R.id.marcaPieza)
        val imageView: ImageView = view.findViewById(R.id.piezaImagen)
    }
}
