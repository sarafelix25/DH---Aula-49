package com.e.aula49_firebasestorage

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference

    //codigo para intent da imagem, pode ser qualquer numero, mas na hora de recuperar tem que ser o mesmo
    private val CODE_IMG = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        config()

        fb_upload.setOnClickListener {
            //vai pegar o recurso, e mandar pra activityonResult e fazer as coisa pra mandar pro firebase
            getRes()
        }


    }

    fun config() {
        alertDialog = SpotsDialog.Builder().setContext(this).build()
        storageReference = FirebaseStorage.getInstance().getReference("img")
    }

    fun getRes() {
        //passa tipo para a intent, action que é o retorno/ação que quero,
        //no start passa a intent com
        val intent = Intent()
        //muita atenção aqui
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Captura Imagem"), CODE_IMG)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CODE_IMG){
            alertDialog .show()

            val uploadFile = storageReference.putFile(data!!.data!!)
            val task = uploadFile.continueWithTask{task ->
                if(task.isSuccessful)
                {
                    Toast.makeText(this, "Imagem Carrregada com sucesso!", Toast.LENGTH_SHORT).show()
                }
                storageReference!!.downloadUrl
            }.addOnCompleteListener{task->
                if(task.isSuccessful){
                    val downloadUri = task.result
                    val url = downloadUri!!.toString().substring(0, downloadUri.toString().indexOf("&token"))
                    Log.i("URL da Imagem", url)
                    alertDialog.dismiss()

                    //passa a imagem que mandamos pro firebase para nosso imageview
                   Picasso.get().load(url).into(iv_res)
                }
            }
        }
    }



    //Funcionou até certa parte, mas não dava pra pegar o url
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        //se a intent que to capturando for igual a fo resquest code
//        if (requestCode == CODE_IMG) {
//            // val uploadFile = storageReference.putFile(data.data) virou esse de baixo qdo fez coisa do nulo
//            val uploadFile =
//                data?.data?.let { storageReference.putFile(it) }?.addOnCompleteListener { task ->
//
//                    if (task.isSuccessful) {
//                        val imgURI = task.result
//                        //divide a string no substring
//                        val imgURL = imgURI.toString().substring(0, imgURI.toString().indexOf("&token"))
//                        Log.i("RESULT", imgURL)
//                    }
//                }?. addOnFailureListener{
//                    Log.i("RESULT", it.toString())
//                }
//        }
//    }

}


