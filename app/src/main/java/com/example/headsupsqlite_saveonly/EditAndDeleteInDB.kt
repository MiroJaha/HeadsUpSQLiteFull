package com.example.headsupsqlite_saveonly

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.muddz.styleabletoast.StyleableToast

class EditAndDeleteInDB : AppCompatActivity() {

    private val dbHelper by lazy { DBHelper(applicationContext) }

    private lateinit var rvList: RecyclerView
    private lateinit var addButton: Button
    private lateinit var searchButton: Button
    private lateinit var searchEntry: EditText
    private lateinit var playList: ArrayList<ArrayList<String>>
    private lateinit var progressDialog : ProgressDialog
    private lateinit var adapter: RVAdaptar
    private lateinit var backImage: ImageView
    private lateinit var dialog: Dialog

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
        setContentView(R.layout.show_data)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        rvList= findViewById(R.id.RVList)
        addButton= findViewById(R.id.addButton)
        searchButton= findViewById(R.id.searchButton)
        searchEntry= findViewById(R.id.searchEntry)
        backImage= findViewById(R.id.backImage)
        dialog= Dialog(this)

        searchButton.isVisible= false
        searchEntry.isVisible= false

        playList= arrayListOf()

        progressDialog = ProgressDialog(this@EditAndDeleteInDB)
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        adapter = RVAdaptar(playList)
        rvList.adapter = adapter
        rvList.layoutManager = LinearLayoutManager(this@EditAndDeleteInDB)

        updateList()
        dialogWindow()

        backImage.setOnClickListener{
            startActivity(Intent(this@EditAndDeleteInDB,MainActivity::class.java))
        }

        addButton.setOnClickListener{
            startActivity(Intent(this@EditAndDeleteInDB,SaveInDB::class.java))
        }

        adapter.setOnItemClickListener(object : RVAdaptar.OnItemClickListener {
            override fun onItemClick(position: Int) {
                getAndSet(position)
                dialog.show()
            }
        })

    }

    private fun dialogWindow(){
        dialog.setContentView(R.layout.edit_celebrity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        nameEntry= dialog.findViewById(R.id.nameEntry)
        taboo1Entry= dialog.findViewById(R.id.taboo1Entry)
        taboo2Entry= dialog.findViewById(R.id.taboo2Entry)
        taboo3Entry= dialog.findViewById(R.id.taboo3Entry)
        updateButton= dialog.findViewById(R.id.updateButton)
        deleteButton= dialog.findViewById(R.id.deleteButton)
        backButton= dialog.findViewById(R.id.backButton)

        backButton.setOnClickListener{
            dialog.dismiss()
        }
        deleteButton.setOnClickListener{
            deleteCelebrity()
        }
        updateButton.setOnClickListener{
            updateCelebrity()
            clear()
        }
    }
    private fun getAndSet(position: Int){
        val celebrity= playList[position]
        id= celebrity[0].toInt()
        name= celebrity[1]
        taboo1= celebrity[2]
        taboo2= celebrity[3]
        taboo3= celebrity[4]
        nameEntry.hint = name
        taboo1Entry.hint = taboo1
        taboo2Entry.hint = taboo2
        taboo3Entry.hint = taboo3
    }

    private fun updateCelebrity() {
        if (checkEntry()){
            val status= dbHelper.updateCelebrity(Information(id,name,taboo1,taboo2,taboo3))
            if (status==1) {
                StyleableToast.makeText(
                    dialog.context.applicationContext,
                    "$name Updated Successfully!!",
                    R.style.mytoast
                )
                updateList()
            }
            else
                StyleableToast.makeText(this, "Error!!", R.style.mytoast)
        }
        else{
            StyleableToast.makeText(this, "Please Enter Correct Values!!", R.style.mytoast)
        }
    }

    private fun deleteCelebrity() {
        AlertDialog.Builder(this)
            .setTitle("Are You Sure You Want To Delete $name")
            .setCancelable(false)
            .setPositiveButton("YES"){_,_ ->
                val status= dbHelper.deleteCelebrity(id)
                if (status==1) {
                    StyleableToast.makeText(
                        this,
                        "Celebrity Deleted Success!!",
                        R.style.mytoast
                    )
                    updateList()
                }
                else
                    StyleableToast.makeText(this, "Error!!", R.style.mytoast)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){dialog,_ -> dialog.cancel() }
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


    private fun updateList(){
        playList.clear()
        val information= dbHelper.gettingData()
        for (celebrity in information){
            playList.add(arrayListOf("${celebrity.pk}",celebrity.name!!,celebrity.taboo1!!,celebrity.taboo2!!,celebrity.taboo3!!))
        }
        sort()
    }

    private fun sort() {
        playList.sortWith( compareBy(String.CASE_INSENSITIVE_ORDER,{it[1]}) )
        adapter.notifyDataSetChanged()
        rvList.scrollToPosition(playList.size-1)
        progressDialog.dismiss()
    }

}