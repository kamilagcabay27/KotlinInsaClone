package com.kamilagcabay.kotlininstaclone.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kamilagcabay.kotlininstaclone.Adapter.PostAdapter
import com.kamilagcabay.kotlininstaclone.Model.Post
import com.kamilagcabay.kotlininstaclone.R
import com.kamilagcabay.kotlininstaclone.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var postAdapter: PostAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore
        postArrayList = ArrayList<Post>()
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(postArrayList)
        binding.recyclerView.adapter = postAdapter
    }

    private fun getData() {
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->


            if (error != null) {
                Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value != null){
                    if (!value.isEmpty){
                        val documents = value.documents

                        postArrayList.clear()

                        for (document in documents) {

                            //Casting
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            //Testing
                            //println(comment)

                            val post = Post(userEmail,comment,downloadUrl)
                            postArrayList.add(post)
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post){
            val intent = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)


        } else if (item.itemId == R.id.logout_account){
            auth.signOut()
            val intent = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}