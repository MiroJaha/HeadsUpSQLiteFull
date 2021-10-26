package com.example.headsupsqlite_saveonly

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val dbHelper by lazy { DBHelper(applicationContext) }

    private lateinit var playList: ArrayList<ArrayList<String>>
    private lateinit var numberList: ArrayList<Int>
    private var startCheck= false
    private var timeCheck= false
    private var isPortrait= true
    private lateinit var timeTV: TextView
    private lateinit var informationTV: TextView
    private lateinit var taboosTV: TextView
    private lateinit var startButton: Button
    private lateinit var viewCelebrityButton: Button
    private lateinit var saveDBButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeTV= findViewById(R.id.timeTV)
        informationTV= findViewById(R.id.informationTV)
        startButton= findViewById(R.id.startButton)
        viewCelebrityButton= findViewById(R.id.viewCelebrityButton)
        taboosTV= findViewById(R.id.taboosTV)
        playList= arrayListOf()
        numberList= arrayListOf()

        viewCelebrityButton.setOnClickListener{
            startActivity(Intent(this,ShowingData::class.java))
        }

        startButton.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Please Chose You Database")
                .setPositiveButton("API"){_,_ ->
                    updateList()
                    saveDBButton.isVisible= false
                    viewCelebrityButton.isVisible= false
                    startButton.isVisible= false
                    timeTV.text = "Time: 60"
                    countDown()
                }
                .setNegativeButton("SQLite"){_,_ ->
                    updateFromSQLite()
                    saveDBButton.isVisible= false
                    viewCelebrityButton.isVisible= false
                    startButton.isVisible= false
                    timeTV.text = "Time: 60"
                    countDown()
                }
                .setCancelable(false)
                .show()
        }

        saveDBButton= findViewById(R.id.saveDBButton)
        saveDBButton.setOnClickListener{
            startActivity(Intent(this,EditAndDeleteInDB::class.java))
        }

    }

    private fun countDown(){
        if (!isPortrait) {
            requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val dialog = ProgressDialog(this@MainActivity)

        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                dialog.setTitle("The Game Will Start in: ${millisUntilFinished / 1000}")
                dialog.show()
            }
            override fun onFinish() {
                if (!isPortrait) {
                    requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                dialog.dismiss()
                informationTV.text= "Please Rotate Device"
                startCheck= true
                timeCheck= true
            }
        }.start()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when {
            newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE -> {
                isPortrait= false
                if (startCheck) {
                    if (playList.size != numberList.size) {
                        var random = Random.nextInt(0, playList.size)
                        while (true) {
                            if (numberList.contains(random))
                                random = Random.nextInt(0, playList.size)
                            else {
                                numberList.add(random)
                                break
                            }
                        }
                        informationTV.setTextColor(Color.BLACK)
                        informationTV.text = playList[random][1]
                        taboosTV.text =
                            "${playList[random][2]}\n${playList[random][3]}\n${playList[random][4]}"
                        taboosTV.isVisible = true
                        startTime()
                    }
                    else{
                        timeTV.text = "Time: 60"
                        startButton.text= "Play Again"
                        informationTV.text ="No More Celebrity to Show!"
                        informationTV.setTextColor(Color.GRAY)
                        startCheck= false
                        saveDBButton.isVisible= true
                        startButton.isVisible= true
                        viewCelebrityButton.isVisible= true
                        taboosTV.isVisible= false
                        if (!isPortrait) {
                            requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                }
            }
            newConfig.orientation=== Configuration.ORIENTATION_PORTRAIT -> {
                isPortrait=true
                if (startCheck){
                    informationTV.setTextColor(Color.GRAY)
                    taboosTV.isVisible= false
                    informationTV.text= "Please Rotate Device"
                }
            }
        }
    }

    private fun startTime(){
        if (timeCheck){
            timeCheck= false
            object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeTV.text ="Time: ${millisUntilFinished / 1000}"
                }
                override fun onFinish() {
                    timeTV.text = "Time Finish!"
                    startButton.text= "Play Again"
                    informationTV.text ="Game Over!"
                    informationTV.setTextColor(Color.GRAY)
                    startCheck= false
                    saveDBButton.isVisible= true
                    startButton.isVisible= true
                    viewCelebrityButton.isVisible= true
                    taboosTV.isVisible= false
                     if (!isPortrait) {
                         requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
            }.start()
        }
    }

    private fun updateList(){
        playList.clear()
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        apiInterface?.getInformation()?.enqueue(object: Callback<List<Information>> {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onResponse(call: Call<List<Information>>, response: Response<List<Information>>) {
                try {
                    for (person in response.body()!!){
                        playList.add(arrayListOf(
                            person.pk!!.toString(),
                            person.name!!,
                            person.taboo1!!,
                            person.taboo2!!,
                            person.taboo3!!
                        ))
                    }
                }
                catch (e: Exception){
                    Log.d("MyInformation","failed $e")
                }
            }

            override fun onFailure(call: Call<List<Information>>, t: Throwable) {
                //Log.d("MyInformation","failed \n$t")
                Toast.makeText(this@MainActivity,"Failed ", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateFromSQLite(){
        playList.clear()
        var count=0
        val list= dbHelper.gettingData()
        for (celebrity in list){
            playList.add(arrayListOf(
                count++.toString(),
                celebrity.name!!,
                celebrity.taboo1!!,
                celebrity.taboo2!!,
                celebrity.taboo3!!
            ))
        }
    }

}