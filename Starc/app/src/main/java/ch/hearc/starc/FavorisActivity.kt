package ch.hearc.starc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.ArrayList

class FavorisActivity : AppCompatActivity(){

    //List of constellations name
    var constellationFavoris :ArrayList<String> = arrayListOf()
    //Adapter
    var adapter: ConstellationAdapter? = null

    lateinit var toolBar : Toolbar

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoris)
        //Load the favoris
        loadData()
        adapter = ConstellationAdapter(constellationFavoris)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        toolBar = findViewById(R.id.toolbar)
        toolBar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        })
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        val sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val constellationFavorisGSON = sharedPreferences.getString("favoris", null)
        val itemType = object : TypeToken<ArrayList<String>>() {}.type
        if (constellationFavorisGSON != null){
            val list :ArrayList<String> = Gson().fromJson(constellationFavorisGSON, itemType)
            if(!list.isNullOrEmpty()) {
                constellationFavoris = Gson().fromJson(constellationFavorisGSON, itemType)
            }
        }
    }
}