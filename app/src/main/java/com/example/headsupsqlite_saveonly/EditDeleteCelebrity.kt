package com.example.headsupsqlite_saveonly

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditDeleteCelebrity : AppCompatActivity() {

    private lateinit var nameEntry: EditText
    private lateinit var taboo1Entry: EditText
    private lateinit var taboo2Entry: EditText
    private lateinit var taboo3Entry: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button
    private var id: Int =0
    private lateinit var name: String
    private lateinit var taboo1: String
    private lateinit var taboo2: String
    private lateinit var taboo3: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_celebrity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameEntry= findViewById(R.id.nameEntry)
        taboo1Entry= findViewById(R.id.taboo1Entry)
        taboo2Entry= findViewById(R.id.taboo2Entry)
        taboo3Entry= findViewById(R.id.taboo3Entry)
        updateButton= findViewById(R.id.updateButton)
        deleteButton= findViewById(R.id.deleteButton)
        backButton= findViewById(R.id.backButton)

        getAndSet()

        backButton.setOnClickListener{
            startActivity(Intent(this,ShowingData::class.java))
        }
        deleteButton.setOnClickListener{
            deleteCelebrity()
        }
        updateButton.setOnClickListener{
            updateCelebrity()
            clear()
        }

    }

    private fun getAndSet(){
        id= intent.extras?.getString("id")!!.toInt()
        name= intent.extras?.getString("name")!!
        taboo1= intent.extras?.getString("taboo1")!!
        taboo2= intent.extras?.getString("taboo2")!!
        taboo3= intent.extras?.getString("taboo3")!!
        nameEntry.hint = name
        taboo1Entry.hint = taboo1
        taboo2Entry.hint = taboo2
        taboo3Entry.hint = taboo3
    }

    private fun updateCelebrity() {
        if (checkEntry()){
            val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
            apiInterface?.updateCelebrity(id, Information(id, name, taboo1, taboo2, taboo3))
                ?.enqueue(object: Callback<Information> {
                    override fun onResponse(call: Call<Information>, response: Response<Information>) {
                        Toast.makeText(applicationContext, "User Update Success!", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(call: Call<Information>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error!", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        else{
            Toast.makeText(this,"Please Enter Correct Values",Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteCelebrity() {
        AlertDialog.Builder(this)
            .setTitle("Are You Sure You Want To Delete $name")
            .setCancelable(false)
            .setPositiveButton("YES"){_,_ ->
                val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
                apiInterface?.deleteCelebrity(id)
                    ?.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            Toast.makeText(
                                applicationContext,
                                "User Delete Success!",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@EditDeleteCelebrity,ShowingData::class.java))
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(applicationContext, "Error!", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("No"){dialog,_ -> dialog.cancel() }
            .show()
    }

    private fun checkEntry(): Boolean{
        var checking= false
        if(nameEntry.text.isNotBlank()) {
            checking= true
            name= nameEntry.text.toString()
        }
        if(taboo1Entry.text.isNotBlank()) {
            checking= true
            taboo1= taboo1Entry.text.toString()
        }
        if(taboo2Entry.text.isNotBlank()) {
            checking= true
            taboo2= taboo2Entry.text.toString()
        }
        if(taboo3Entry.text.isNotBlank()) {
            checking= true
            taboo3= taboo3Entry.text.toString()
        }
        return checking
    }

    private fun clear(){
        nameEntry.text.clear()
        nameEntry.hint = name
        taboo1Entry.text.clear()
        taboo1Entry.hint = taboo1
        taboo2Entry.text.clear()
        taboo2Entry.hint = taboo2
        taboo3Entry.text.clear()
        taboo3Entry.hint = taboo3
        val view: View? = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}