package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationRequest
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.io.IOException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            this
        )

        binding.getLocatiion.setOnClickListener() {
            checkLocationPermssion()

        }

        setSupportActionBar(binding.toolbar)


    }


    private fun checkLocationPermssion() {

        if(ActivityCompat.checkSelfPermission(

                    this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )==PackageManager.PERMISSION_GRANTED){
            checkGPS()

        }
        else{

                ActivityCompat.requestPermissions(this,arrayof(Manifest.permission.ACCESS_FINE_LOCATION),100)




        }
    }

    private fun checkGPS() {
       locationRequest=LocationRequest.create()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.inerval=5000
        locationRequest.fastestInterval=200

        val builder=LocationSettingsRequest.Builder().addLocationRequest(locationRequest )


        builder.setAlwaysShow(true)
        var result=LocationServices.getSettingsClient(
            this.applicationContext
        )
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener{
            task ->
            try{
                val response=task.getResult(ApiException::class.java )
                getUserLocation()

            }catch (
                e:ApiException) {
                e.printStackTrace()
             when(e.statusCode){ LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->try{ //send the rqst for enable gps
            val resolveApiException= e as ResolvableApiException resolveApiException.startResolutionForResult(this,200)
             }catch(sendIntentException:IntentSender.SendIntentException){}
                 LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                 }
             }
             }

            }
        }



    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task -> val location =task.getResult()
            if (location != null){
                try{
                    val geocoder: Geocoder(this.getDefault())
                    val address=geocoder.getFromLocation(location.latitude,location.longitude, 1)
                    val address_line= address[0].getAddressLine(0)
                    binding.locationText.setText(address_line)
                    val address_location=address[0].getAddressLine(0)
                    openLocation(address_location.toString())


                }catch (e:IOException){

                }
            }
        }
    }

    private fun openLocation(location: String) {

        binding.locationText.setOnClickListener(){
            if(!binding.locationText.text.isEmpty()){
                val uri= Uri.parse("geo:0,0?q=$location")
                val  intent =Intent(Intent.ACTION_VIEW,uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)


            }
        }



    }


}