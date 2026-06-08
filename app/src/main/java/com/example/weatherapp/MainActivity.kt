package com.example.weatherapp

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("Ahmedabad")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    fetchWeatherData(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

        })
    }
    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(city, "601450123f05c4609052614f4f035d54", "metric")
        response.enqueue(object : Callback<WeatherModel> {
            override fun onResponse(
                call: Call<WeatherModel?>?,
                response: Response<WeatherModel?>?
            ) {
                val responsebody = response!!.body()
                if (response.isSuccessful && responsebody != null) {
                    val temperature = responsebody.main.temp.toString()
                    val wind = responsebody.wind.speed.toString()
                    val humidity = responsebody.main.humidity.toString()
                    val sealevel = responsebody.main.sea_level.toString()
                    val description = responsebody.weather[0].description
                    val condition = responsebody.weather[0].main
                    val sunrise = formatTime(responsebody.sys.sunrise)
                    val sunset = formatTime(responsebody.sys.sunset)
                    val maxTamp = responsebody.main.temp_max.toString()
                    val minTamp = responsebody.main.temp_min.toString()
                    val cityName = responsebody.name
                    val day = responsebody.dt * 1000
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val sdtf = SimpleDateFormat("EEEE", Locale.getDefault())
                    val day1 = sdf.format(java.util.Date(day))
                    val date = sdtf.format(java.util.Date(day))
                    binding.textView4.text = temperature
                    binding.humidity.text = "${humidity} %"
                    binding.windSpeed.text = "${wind} m/s"
                    binding.conditions.text = description
                    binding.sunriseTime.text = sunrise
                    binding.sunsetTime.text = sunset
                    binding.seaSpeed.text = "${sealevel} hPa"
                    binding.textView6.text = minTamp
                    binding.textView7.text = maxTamp
                    binding.textView2.text = cityName
                    binding.textView12.text = date
                    binding.textView13.text = day1
                    binding.textView18.text = condition
                    changeBackground(condition)
                }
            }

            override fun onFailure(
                call: Call<WeatherModel?>?,
                t: Throwable?
            ) {
            }

        })
    }

    private fun changeBackground(condition: String) {
        when(condition){
            "Clouds","Atmosphere"  -> {
                binding.root.setBackgroundResource(R.drawable.sky_few_cloud)
                binding.lottieAnimationView.setAnimation(R.raw.fog)
            }
            "Smoke","Haze","Overcast" ,"Mist" ,"Foggy"-> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.fog)
            }
            "Sunny","Clear","Clear Sky"-> {
                binding.root.setBackgroundResource(R.drawable.clear_sky_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Snow","Blizzard","Light Snow","Moderate Snow","Heavy Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Rain","Drizzle" ,"Showers","Heavy Rain","Moderate Rain","Light Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
    }
    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(date)
    }
}