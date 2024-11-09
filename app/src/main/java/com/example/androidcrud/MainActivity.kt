package com.example.androidcrud

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidcrud.databinding.ActivityMainBinding

import kotlinx.coroutines.*
import retrofit2.Response

class MainActivity : AppCompatActivity(), UsuarioAdapter.OnItemClicked {

    lateinit var binding: ActivityMainBinding

    lateinit var adatador: UsuarioAdapter

    var listaUsuarios = arrayListOf<Usuario>()


    var usuario = Usuario(-1, "","")

    var isEditando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

        obtenerUsuarios()

        binding.btnAddUpdate.setOnClickListener {
            var isValido = validarCampos()
            if (isValido) {
                if (!isEditando) {
                    agregarUsuario()
                } else {
                    actualizarUsuario()
                }
            } else {
                Toast.makeText(this, "Se deben llenar los campos", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun setupRecyclerView() {
        adatador = UsuarioAdapter(this, listaUsuarios)
        adatador.setOnClick(this@MainActivity)
        binding.rvUsuarios.adapter = adatador

    }

    fun validarCampos(): Boolean {
        return !(binding.etNombre.text.isNullOrEmpty() || binding.etEmail.text.isNullOrEmpty())
    }

    fun obtenerUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {

            try{
                val call = RetrofitClient.webService.obtenerUsuarios()


            runOnUiThread {
                if (call.isSuccessful) {
                    listaUsuarios = call.body()!!.listaUsuarios
                    setupRecyclerView()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR CONSULTAR TODOS", Toast.LENGTH_LONG).show()
                }
            }
            }catch(e: Exception){
                //code that handles exception
                println("code below exception ...")
            }
        }
    }

    fun agregarUsuario() {

        this.usuario.id = -1
        this.usuario.Nombre = binding.etNombre.text.toString()
        this.usuario.Correo = binding.etEmail.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.agregarUsuario(usuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()

                } else {
                    Toast.makeText(this@MainActivity, "ERROR ADD", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun actualizarUsuario() {
        try {

        this.usuario.Nombre = binding.etNombre.text.toString()
        this.usuario.Correo = binding.etEmail.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.actualizarUsuario(usuario.id, usuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG)
                        .show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()
                    binding.btnAddUpdate.text = "Agregar Usuario"
                    binding.btnAddUpdate.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#00FF00"))
                    isEditando = false
                }
            }
        }
    }catch(e: Exception){
        //code that handles exception
        println("code below exception ...")
    }
    }

    fun limpiarCampos() {
        binding.etNombre.setText("")
        binding.etEmail.setText("")
    }

    fun limpiarObjeto() {
        this.usuario.id = -1
        this.usuario.Nombre = ""
        this.usuario.Correo = ""
    }

    override fun editarUsuario(usuario: Usuario) {
        binding.etNombre.setText(usuario.Nombre)
        binding.etEmail.setText(usuario.Correo)
        binding.btnAddUpdate.text = "Actualizar Usuario"
        binding.btnAddUpdate.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E08ED4"))
        this.usuario = usuario
        isEditando = true
    }

    override fun borrarUsuario(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.borrarUsuario(idUsuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                }
            }
        }
    }
}