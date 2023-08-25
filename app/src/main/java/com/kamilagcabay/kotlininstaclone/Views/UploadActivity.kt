package com.kamilagcabay.kotlininstaclone.Views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kamilagcabay.kotlininstaclone.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var imageData : Uri? = null
    private lateinit var auth :FirebaseAuth
    private lateinit var  firestore: FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage


    }

    fun shareButtonClick(view: View) {

        //universal unique id
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference //Bu kodu yazınca bize firebase'de oluşturduğumuz Storage kısmında oluşan boşluğu temsil ediyor
        //bunun anlamı images adlı bir klasör aç ve içine image.jpg diye bir dosya koy demek
        val imageReference = reference.child("images").child(imageName)


        if (imageData != null) {
            imageReference.putFile(imageData!!).addOnSuccessListener {
                //download url -> firestore
                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {uri ->
                    val downloadUrl = uri.toString()

                    if (auth.currentUser != null){
                        val postMap = hashMapOf<String, Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("comment",binding.commentText.text.toString())
                        postMap.put("date",Timestamp.now())

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {

                            finish()


                        }.addOnFailureListener{
                            Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }


                }


            }.addOnFailureListener{
                Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
    //Choose Image and Permission Controllers
    fun selectImage(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if (ContextCompat.checkSelfPermission(this@UploadActivity,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@UploadActivity,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for Media",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                } else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                //go to Gallery
                val intentFromGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentFromGallery)
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data

                if (intentFromResult != null){
                    imageData = intentFromResult.data
                    binding.uploadImage.setImageURI(imageData)
                }
            }

        })

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), ActivityResultCallback {result ->

            if (result){
                val intentFromGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentFromGallery)
            } else {
                Toast.makeText(this@UploadActivity,"Permission needed for Gallery",Toast.LENGTH_LONG).show()
            }

        })
    }

}