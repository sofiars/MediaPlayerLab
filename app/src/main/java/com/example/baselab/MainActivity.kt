package com.example.baselab

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    companion object{
        var OPEN_DIRECTORY_REQUEST_CODE=1
    }

    private var contador : Int = 0
    private var stopping = true
    private var archivos :MutableList<DocumentFile> = mutableListOf()
    private lateinit var stop: Button
    private lateinit var anterior: Button
    private lateinit var reproducir: Button
    private lateinit var next: Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        anterior = findViewById(R.id.anterior)
        reproducir = findViewById(R.id.botonReproducir)
        stop = findViewById(R.id.botonPausa)
        next=findViewById(R.id.next)
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
        setOnClickListeners(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OPEN_DIRECTORY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                var directoryUri = data?.data ?:return
                val rootTree = DocumentFile.fromTreeUri(this,directoryUri )
                for(file in rootTree!!.listFiles()){
                    try {
                        file.name?.let { Log.e("Archivo", it) }
                        archivos.add(file)
                    }catch (e: Exception){
                        Log.e("Error", "No pude ejecutar el archivo" + file.uri)
                    }
                }
                mediaPlayer = MediaPlayer.create(this, archivos[contador].uri )
            }
        }
    }

    private fun setOnClickListeners(context: Context) {
        reproducir.setOnClickListener {
            if(stopping){
                stopping = false
                mediaPlayer.start()
                Toast.makeText(context, "Playing music...", Toast.LENGTH_SHORT).show()
                reproducir.setText("Pause")
            }else{
                stopping = true
                mediaPlayer.pause()
                Toast.makeText(context, "Stopping music...", Toast.LENGTH_SHORT).show()
                reproducir.setText("Play")
            }

        }

        stop.setOnClickListener {
            contador = 0
            stopping = true
            reproducir.setText("Play")
            mediaPlayer.stop()
            Toast.makeText(context, "Stopping music...", Toast.LENGTH_SHORT).show()
            mediaPlayer = MediaPlayer.create(this, archivos[contador].uri )
        }
        anterior.setOnClickListener{
            if(contador - 1 < 0){
                mediaPlayer.stop()
                contador = archivos.size-1
                mediaPlayer = MediaPlayer.create(context, archivos[contador].uri )
                mediaPlayer.start()
            }else{
                mediaPlayer.stop()
                contador --
                mediaPlayer = MediaPlayer.create(context, archivos[contador].uri )
                mediaPlayer.start()

            }
        }
        next.setOnClickListener{
            if(contador + 1 > archivos.size - 1){
                mediaPlayer.stop()
                contador = 0
                mediaPlayer = MediaPlayer.create(context, archivos[contador].uri )
                mediaPlayer.start()
            }else{
                mediaPlayer.stop()
                contador ++
                mediaPlayer = MediaPlayer.create(context, archivos[contador].uri )
                mediaPlayer.start()
            }
        }
    }
}