package com.example.headsupsqlite_saveonly

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.random.Random

class ShowingData : AppCompatActivity() {

    private lateinit var rvList: RecyclerView
    private lateinit var addButton: Button
    private lateinit var searchButton: Button
    private lateinit var searchEntry: EditText
    private lateinit var playList: ArrayList<ArrayList<String>>
    private lateinit var progressDialog : ProgressDialog
    private lateinit var adapter: RVAdaptar
    private lateinit var backImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_data)

        rvList= findViewById(R.id.RVList)
        addButton= findViewById(R.id.addButton)
        searchButton= findViewById(R.id.searchButton)
        searchEntry= findViewById(R.id.searchEntry)
        backImage= findViewById(R.id.backImage)

        searchButton.isVisible= true
        searchEntry.isVisible= true

        playList= arrayListOf()

        progressDialog = ProgressDialog(this@ShowingData)
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        adapter = RVAdaptar(playList)
        rvList.adapter = adapter
        rvList.layoutManager = LinearLayoutManager(this@ShowingData)

        updateList()

        backImage.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }

        addButton.setOnClickListener{
            startActivity(Intent(this,AddNewCelebrity::class.java))
        }

        adapter.setOnItemClickListener(object : RVAdaptar.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@ShowingData, EditDeleteCelebrity::class.java)
                intent.putExtra("id",playList[position][0])
                intent.putExtra("name",playList[position][1])
                intent.putExtra("taboo1",playList[position][2])
                intent.putExtra("taboo2",playList[position][3])
                intent.putExtra("taboo3",playList[position][4])
                startActivity(intent)
            }
        })

        searchButton.setOnClickListener{
            if (searchEntry.text.isNotBlank()){
                searchCelebrity()
            }
            else{
                Toast.makeText(this, "Please Enter Celebrity Name", Toast.LENGTH_LONG).show()
            }
            searchEntry.text.clear()
            val view: View? = this.currentFocus
            if (view != null) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

    }

    private fun searchCelebrity() {
        var exist = false
        for (i in 0 until playList.size){
            if (playList[i][1].equals(searchEntry.text.toString(),true)){
                exist=true
                val intent= Intent(this,EditDeleteCelebrity::class.java)
                intent.putExtra("id",playList[i][0])
                intent.putExtra("name",playList[i][1])
                intent.putExtra("taboo1",playList[i][2])
                intent.putExtra("taboo2",playList[i][3])
                intent.putExtra("taboo3",playList[i][4])
                startActivity(intent)
                searchEntry.text.clear()
            }
        }
        if (!exist){
            Toast.makeText(this, "Celebrity Not Exist", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateList(){
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        apiInterface?.getInformation()?.enqueue(object: Callback<List<Information>> {
            override fun onResponse(call: Call<List<Information>>, response: Response<List<Information>>) {
                try {
                    playList.clear()
                    for (person in response.body()!!){
                        playList.add(arrayListOf(
                            person.pk!!.toString(),
                            person.name!!,
                            person.taboo1!!,
                            person.taboo2!!,
                            person.taboo3!!
                        ))
                    }
                    sort()
                }
                catch (e: Exception){
                    Log.d("MyInformation","failed $e")
                }
            }

            override fun onFailure(call: Call<List<Information>>, t: Throwable) {
                //Log.d("MyInformation","failed \n$t")
                Toast.makeText(this@ShowingData,"Failed ", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sort() {
        playList.sortWith( compareBy(String.CASE_INSENSITIVE_ORDER,{it[1]}) )

        adapter.notifyDataSetChanged()
        rvList.scrollToPosition(playList.size-1)

        progressDialog.dismiss()
    }

}