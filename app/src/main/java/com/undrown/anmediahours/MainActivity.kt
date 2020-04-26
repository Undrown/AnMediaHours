package com.undrown.anmediahours

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.format.DateFormat.format
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.net.URLEncoder
import java.util.*

class MainActivity : AppCompatActivity() {
    val host = "https://anmedia-server.000webhostapp.com"
    private val millisInMinute:Long = 1000L*60
    private val millisInHour:Long = 60*millisInMinute
    private var day = Calendar.getInstance()
    private var timeStart:Long = 10*millisInHour
    private var timeEnd:Long = 19*millisInHour
    private var uid:Int = 0
    private var phoneNum:Long = 0L
    private var perHour:Long = 0L
    private var name:String? = "default"
    private var queue: RequestQueue? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queue = Volley.newRequestQueue(baseContext)
        uid = this.getPreferences(Context.MODE_PRIVATE)
            .getInt("com.undrown.anmediahours.uid", 0)
        //set ContentView by uid
        if(uid == 0) {
            setContentView(R.layout.activity_login)
            //set button_login onclick
            findViewById<Button>(R.id.button_login).setOnClickListener {
                login()
            }
        }
        else {
            loadData()
            setContentView(R.layout.activity_main)
            refreshTimes()
            //onClickListeners for small buttons ------------------------------------------------
            findViewById<Button>(R.id.time_start_up).setOnClickListener {
                if(timeStart <= 23*millisInHour)
                    timeStart += 1*millisInHour
                refreshTimes()
            }
            findViewById<Button>(R.id.time_start_down).setOnClickListener {
                if(timeStart > 0)
                    timeStart -= 1*millisInHour
                refreshTimes()
            }
            findViewById<Button>(R.id.time_end_up).setOnClickListener {
                if(timeEnd < 23*millisInHour)
                timeEnd += 1*millisInHour
                if(timeEnd == 23*millisInHour)
                    timeEnd += 59*millisInMinute
                refreshTimes()
            }
            findViewById<Button>(R.id.time_end_down).setOnClickListener {
                if(timeEnd > 23*millisInHour)
                    timeEnd -= 59*millisInMinute
                else if(timeEnd > 0)
                    timeEnd -= 1*millisInHour
                refreshTimes()
            }
            //-----------------------------------------------------------------------------------
            findViewById<TextView>(R.id.date).setOnClickListener {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Дата")
                alert.setMessage("Выбери дату")
                val input = CalendarView(this)
                input.date = day.timeInMillis
                input.setOnDateChangeListener{ _, i, i2, i3 ->
                    day = Calendar.Builder().setDate(i, i2, i3).build()
                }
                alert.setView(input)
                alert.setPositiveButton("Ok") { _, _ ->
                    findViewById<TextView>(R.id.date).text =
                        format("E, d MMM", day)
                }
                alert.setNegativeButton("Cancel") { _, _ ->
                    // Canceled.
                }
                alert.show()
            }

            findViewById<Button>(R.id.send_button).setOnClickListener {
                val entry = Entry(
                    timeStart = day.timeInMillis + timeStart,
                    timeEnd = day.timeInMillis + timeEnd,
                    comment = findViewById<TextView>(R.id.comment).text.toString()
                )
                postData(entry)
            }
        }
    }

    private fun refreshTimes(){
        findViewById<TextView>(R.id.date).text =
            format("E, d MMM", day)
        val addedNull1 = if ((timeStart%millisInHour)/millisInMinute == 0L) "0" else ""
        val addedNull2 = if ((timeEnd%millisInHour)/millisInMinute == 0L) "0" else ""
        findViewById<TextView>(R.id.time_start).text =
            "%d:%d%s"
                .format(timeStart/millisInHour, (timeStart%millisInHour)/millisInMinute, addedNull1)
        findViewById<TextView>(R.id.time_end).text =
            "%d:%d%s"
                .format(timeEnd/millisInHour, (timeEnd%millisInHour)/millisInMinute, addedNull2)
    }

    private fun login(){
        //var possiblePhoneNum = 0L
        //if (ActivityCompat.checkSelfPermission(
        //        this,
        //        Manifest.permission.READ_SMS
        //    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        //        this,
        //        Manifest.permission.READ_PHONE_NUMBERS
        //    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        //        this,
        //        Manifest.permission.READ_PHONE_STATE
        //    ) != PackageManager.PERMISSION_GRANTED
        //) {
        //    possiblePhoneNum = 0L
        //}else {
        //    possiblePhoneNum = (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
        //        .line1Number.toLong()
        //}
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Вход")
        alert.setMessage("Введите номер телефона")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "9999104955"
        //input.text.append(possiblePhoneNum.toString())
        alert.setView(input)
        alert.setPositiveButton("Ok") { _, _ ->
            val phone = input.text.toString().toLong()
            getData(phone)
        }
        alert.setNegativeButton("Cancel") { _, _ ->
            // Canceled.
        }
        alert.show()
    }

    private fun getData(phone:Long){
        val address = "$host/get_name.php"
        val url = "$address?phone=$phone"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                Toast.makeText(applicationContext, response, Toast.LENGTH_SHORT)
                    .show()
                //uid -- name -- phoneNum -- comment
                if("ERROR_INVALID_UID" in response){
                    Toast.makeText(applicationContext, "ERROR GETTING UID", Toast.LENGTH_SHORT)
                        .show()
                }else{
                    val result = response.split(" -- ")
                    //save data
                    this.getPreferences(Context.MODE_PRIVATE).edit()
                        .putInt("com.undrown.anmediahours.uid", result[0].toInt())
                        .apply()
                    this.getPreferences(Context.MODE_PRIVATE).edit()
                        .putString("com.undrown.anmediahours.name", result[1])
                        .apply()
                    this.getPreferences(Context.MODE_PRIVATE).edit()
                        .putLong("com.undrown.anmediahours.phone", result[2].toLong())
                        .apply()
                    this.getPreferences(Context.MODE_PRIVATE).edit()
                        .putLong("com.undrown.anmediahours.per_hour", result[3].toLong())
                        .apply()
                    loadData()
                    setContentView(R.layout.activity_main)
                }
            },
            Response.ErrorListener { Toast.makeText(applicationContext, "NETWORK ERROR", Toast.LENGTH_SHORT)
                .show()}
        )
        queue?.add(stringRequest)
    }

    private fun loadData(){
        uid = this.getPreferences(Context.MODE_PRIVATE)
                .getInt("com.undrown.anmediahours.uid", 0)
        phoneNum =
                this.getPreferences(Context.MODE_PRIVATE)
                        .getLong("com.undrown.anmediahours.phoneNum", 0L)
        perHour =
                this.getPreferences(Context.MODE_PRIVATE)
                        .getLong("com.undrown.anmediahours.per_hour", 0L)
        name =
                this.getPreferences(Context.MODE_PRIVATE)
                        .getString("com.undrown.anmediahours.name", "default")
    }

    private fun postData(data:Entry){
        val address = "$host/set_entry.php"
        val comment = URLEncoder.encode(data.comment, "utf-8").replace("\n", "")
        val url = "$address?uid=$uid&time_start=${data.timeStart}&time_end=${data.timeEnd}&comment=${comment}"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener {response ->
                if(response == "OK")
                    Toast.makeText(applicationContext, response, Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(applicationContext, "DATA_ERROR", Toast.LENGTH_SHORT)
                        .show()
            },
            Response.ErrorListener { Toast.makeText(applicationContext, "NETWORK ERROR", Toast.LENGTH_SHORT)
                .show()}
        )
        queue?.add(stringRequest)
    }
}
