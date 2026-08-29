package com.example.wanderlist.data.remote

//sadrzi listu jer prognoza vraca oko 40 vremenskih tocaka (po 3h kroz 5 dana),
//a ne samo jednu vrijednost
data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val main: MainWeather,
    val weather: List<WeatherDescription>
)