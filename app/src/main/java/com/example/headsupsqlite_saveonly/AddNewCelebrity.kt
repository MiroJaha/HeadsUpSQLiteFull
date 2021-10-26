package com.example.headsupsqlite_saveonly

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewCelebrity : AppCompatActivity() {

    private lateinit var nameEntry:EditText
    private lateinit var taboo1Entry:EditText
    private lateinit var taboo2Entry:EditText
    private lateinit var taboo3Entry:EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_celebrity)

        nameEntry= findViewById(R.id.nameEntry)
        taboo1Entry= findViewById(R.id.taboo1Entry)
        taboo2Entry= findViewById(R.id.taboo2Entry)
        taboo3Entry= findViewById(R.id.taboo3Entry)
        saveButton= findViewById(R.id.saveButton)
        backButton= findViewById(R.id.backButton)

        backButton.setOnClickListener{
            startActivity(Intent(this,ShowingData::class.java))
        }
        saveButton.setOnClickListener{
            if (checkEntry()){
              addNewCelebrity()
            }
            else
                Toast.makeText(this,"Please Enter Correct Values",Toast.LENGTH_LONG).show()
            clear()
        }
    }

    private fun checkEntry(): Boolean{
        return when {
            nameEntry.text.isBlank() -> false
            taboo1Entry.text.isBlank() -> false
            taboo2Entry.text.isBlank() -> false
            else -> taboo3Entry.text.isNotBlank()
        }
    }

    private fun addNewCelebrity(){
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        apiInterface?.addCelebrity(
            Information(
                1,
                nameEntry.text.toString(),
                taboo1Entry.text.toString(),
                taboo2Entry.text.toString(),
                taboo3Entry.text.toString()
            )
        )
            ?.enqueue(object : Callback<Information> {
                override fun onResponse(
                    call: Call<Information>,
                    response: Response<Information>
                ) {
                    Toast.makeText(applicationContext, "Save Success!", Toast.LENGTH_SHORT)
                        .show()

                }

                override fun onFailure(call: Call<Information>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun clear(){
        nameEntry.text.clear()
        taboo1Entry.text.clear()
        taboo2Entry.text.clear()
        taboo3Entry.text.clear()
        val view: View? = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}