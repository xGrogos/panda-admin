package com.panda.admin.navbar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.panda.admin.R
import com.panda.admin.firebase.pieza
import java.io.ByteArrayOutputStream

class crear : Fragment() {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val EXTERNAL_PERMISSION_REQUEST_CODE = 1002
        private var CAMERA_EXTERNAL = 0; //1 CAM - 2 EXTERNAL
    }

    lateinit var productStatusSpinner: Spinner;
    lateinit var imageView: ImageView;
    lateinit var photoUri: Uri;
    lateinit var imageBitmap: Bitmap;
    lateinit var categoria:String;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View= inflater.inflate(R.layout.fragment_crear, container, false)

        // Referència de la bbdd
        val database = Firebase.database

        // campos de formulario
        var nombreProducto = view.findViewById(R.id.piezaNombre) as EditText
        var precioProducto = view.findViewById(R.id.piezaPrecio) as EditText
        var marcaProducto = view.findViewById(R.id.piezaMarca) as EditText
        var colorProducto = view.findViewById(R.id.piezaColor) as EditText
        // var caterogiaProducto = view.findViewById(R.id.piezaCategoria) as EditText
        productStatusSpinner = view.findViewById(R.id.piezaCategoria)
        var btnUpload: Button = view.findViewById(R.id.btnAddProduct)
        getSpinnerItemSelected(
            productStatusSpinner,
            resources.getStringArray(R.array.category_array_list),view
        ).toString();
        // Create a storage reference from our app
        val storageRef = Firebase.storage.reference;

        // Create a reference to "mountains.jpg"
        val productRef = storageRef.child("productImg")

        var photoProducto: ImageButton = view.findViewById(R.id.btnPicture)

        photoProducto.setOnClickListener{
            // Si els permisos de càmera no estan validats
            if (!isCameraPermissionGranted()) {
                // Farem una petició de permisos
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                // Sinó farem l'intent de mostrar la càmera
                cameraResult.launch(Intent(ACTION_IMAGE_CAPTURE));
            }
        }

        var galleryProducto: ImageButton = view.findViewById(R.id.btnGallery)
        galleryProducto.setOnClickListener{
            // Si els permisos d'accedir a fitxers externs no estan validats
            if (!isExternalPermissionGranted()) {
                // Farem una petició de permisos
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_PERMISSION_REQUEST_CODE
                )
            } else {
                // Sinó farem l'intent d'obrir la galeria
                val intent =Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                externalResult.launch(intent)
            }
        }

        imageView = view.findViewById(R.id.imageView);

        // Al pulsar el botón de añadir producto hace la siguiente acción
        btnUpload.setOnClickListener{
            var nombre = nombreProducto.getText().toString()
            var precio = precioProducto.getText().toString()
            var marca = marcaProducto.getText().toString()
            var color = colorProducto.getText().toString()
            //var categoria = caterogiaProducto.getText().toString()
            var tallas = arrayListOf(38,39,40,41,42,43,44,45,46,47)
            var imageURL = ""

            if(nombre.isNotEmpty() && precio.isNotEmpty() && marca.isNotEmpty()
                && color.isNotEmpty()
                && (categoria.equals("Pantalon") || categoria.equals("Zapato") || categoria.equals("Camiseta"))){
                if(CAMERA_EXTERNAL == 1){
                    val uploadTask = productRef.child(nombre+marca+color).putFile(getImageUri(requireContext(), imageBitmap)!!)

                    uploadTask.addOnSuccessListener {taskSnapshot->
                        productRef.child(nombre+marca+color).downloadUrl.addOnSuccessListener {
                            Log.e("Firebase", "-->" + it)
                            imageURL = it.toString();

                            val pieza = pieza(nombre,precio,categoria,tallas,color,marca,imageURL)
                            database.getReference("Piezas").push().setValue(pieza);
                            nombreProducto.setText("")
                            precioProducto.setText("")
                            marcaProducto.setText("")
                            colorProducto.setText("")
                            //caterogiaProducto.setText("")

                            val text = "Se ha podido crear el producto."
                            val duration = Toast.LENGTH_SHORT

                            val toast = Toast.makeText(context, text, duration)
                            toast.show()

                        }.addOnFailureListener {
                            Log.e("Firebase", "Failed in downloading")
                        }

                    }.addOnFailureListener {
                        Log.e("Firebase", "Image Upload External KO")
                    }

                }else if(CAMERA_EXTERNAL == 2){
                    val uploadTask = productRef.child(nombre+marca+color).putFile(photoUri)

                    productRef.child(nombre+marca+color).downloadUrl.addOnSuccessListener {
                        Log.e("Firebase", "-->" + it)
                        imageURL = it.toString();

                        val pieza = pieza(nombre,precio,categoria,tallas,color,marca,imageURL)
                        database.getReference("Piezas").push().setValue(pieza);
                        nombreProducto.setText("")
                        precioProducto.setText("")
                        marcaProducto.setText("")
                        colorProducto.setText("")
                        //caterogiaProducto.setText("")

                        val text = "Se ha podido crear el producto."
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(context, text, duration)
                        toast.show()
                    }.addOnFailureListener {
                        Log.e("Firebase", "Failed in downloading")
                    }
                }else{
                    val text = "ERROR: La imagen del producto es obligatorio"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, text, duration)
                    toast.show()
                }

            } else {
                val text = "ERROR: Todo los campos son requiridos"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }
        }
        // Inflate the layout for this fragment
        return view;
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    // Resposta de la càmera
    private val cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent = result.data;
            imageBitmap = intent?.extras?.get("data") as Bitmap;
            imageView.setImageBitmap(imageBitmap);
            CAMERA_EXTERNAL = 1;
        }
    }

    // Resposta de la galeria
    private val externalResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if (result.resultCode === Activity.RESULT_OK && result.data != null) {
            photoUri = result.data?.data!!
            imageView.setImageURI(photoUri);
            CAMERA_EXTERNAL = 2;
        }
    }

    // Permisos camera photo
    private fun isCameraPermissionGranted(): Boolean {
        return context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) } == PackageManager.PERMISSION_GRANTED
    }

    //Permisos external storage
    private fun isExternalPermissionGranted(): Boolean {
        return context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED
    }

    // Resposta a l'acció de l'usuari en validar o no els permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with opening camera
            } else {
                // Permission denied, handle accordingly
            }
        }else if(requestCode == EXTERNAL_PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with opening camera
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
    // Spinner Array Function.
    private fun getSpinnerItemSelected(s: Spinner, array: Array<String>,v:View) {
        if (s != null) {
            val aA = ArrayAdapter(v.context, android.R.layout.simple_spinner_item, array);
            aA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            s.adapter = aA;
            s.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val item = parent!!.getItemAtPosition(position).toString();
                    categoria = item
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }
    }
}