package com.example.workline

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var btnUsar: Button
    private lateinit var coordenadaSeleccionada: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        btnUsar = findViewById(R.id.btnUsar)
        btnUsar.setOnClickListener{

    traducirCoordenadas()

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
activarMiUbicacion()

mMap.setOnMapClickListener {coordenada->

    coordenadaSeleccionada = coordenada
    mMap.clear()
    mMap.addMarker(MarkerOptions().position(coordenada))
    btnUsar.isEnabled=true

}
        // Add a marker in Sydney and move the camera
        val monterrey = LatLng(25.67, -100.31)
        mMap.addMarker(MarkerOptions().position(monterrey).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(monterrey,12f))
    }

    private fun traducirCoordenadas(){

val geocoder = Geocoder(this, Locale.getDefault())

        Thread {
            val direcciones =geocoder.getFromLocation(coordenadaSeleccionada.latitude, coordenadaSeleccionada.longitude,1)

            if(direcciones.size>0) {
                val direccion = direcciones[0].getAddressLine(0)
                val intentDeRegreso= Intent()
                intentDeRegreso.putExtra("ubicacion", direccion)

                setResult(RESULT_OK,intentDeRegreso)
                finish()
            }
        }.start()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    private fun activarMiUbicacion(){


        mMap.isMyLocationEnabled = true
    }
}