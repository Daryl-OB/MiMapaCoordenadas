package com.arathdev.mymap;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    EditText txtLatitud, txtLongitud;
    GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SearchView searchView = findViewById(R.id.searchView);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkIfGPSEnabled();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Llamar a la función para buscar la dirección
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        /*
        LatLng myUbicacion = new LatLng(-11.879024816863266,-77.07325123250484);
        mMap.addMarker(new MarkerOptions().position(myUbicacion).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myUbicacion));

        txtLatitud.setText("" + myUbicacion.latitude);
        txtLongitud.setText("" + myUbicacion.longitude);
        */

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);

        mMap.clear();
        LatLng peru = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(peru).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(peru));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText("" + latLng.latitude);
        txtLongitud.setText("" + latLng.longitude);

        mMap.clear();
        LatLng peru = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(peru).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(peru));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Handle item selection
        if (id == R.id.action_option1) {
            // Navegar a la primera vista
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_option2) {
            // Navegar a la segunda vista
            Intent intent = new Intent(this, Instruccions.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_option3) {
            // Cerrar la aplicación
            finish(); // Esto cierra la actividad actual, y si es la única actividad, cierra la aplicación
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //solicitar los permisos de ubicación en tiempo de ejecución
    private void requestLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else{
            getCurrentLocation();
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else{
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //verificar si el gps está activado
    private void checkIfGPSEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!isGPSEnabled){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            requestLocationPermission();
        }
    }

    private void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null){
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Mi ubicación"));

                    //asignar la ubicación del gps del usuario en el mapa
                    txtLatitud.setText(String.valueOf(userLocation.latitude));
                    txtLongitud.setText(String.valueOf(userLocation.longitude));
                }
                else{
                    Toast.makeText(this, "No se puedo obtener la ubicación", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Función para buscar la dirección
    private void searchLocation(String location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            //busca la dirección ingresada
            List<android.location.Address> addresses = geocoder.getFromLocationName(location, 1);

            if(addresses != null && !addresses.isEmpty()){
                android.location.Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                //actualiza las coordenadas en los campos de texto
                txtLatitud.setText(String.valueOf(latLng.latitude));
                txtLongitud.setText(String.valueOf(latLng.longitude));

                //limpia el mapa y añade un marcador en la ubicación buscada
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else{
                Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al buscar la ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyCoordinates(View view){
        EditText txtLatitud = findViewById(R.id.txtLatitud);
        EditText txtLongitud = findViewById(R.id.txtLongitud);

        String latitud = txtLatitud.getText().toString();
        String longitud = txtLongitud.getText().toString();

        // Verifica que las coordenadas no estén vacías
        if (latitud.isEmpty() || longitud.isEmpty()) {
            Toast.makeText(this, "Coordenadas incompletas", Toast.LENGTH_SHORT).show();
            return;
        }

        //Concatenar las coordenadas en formato "latitud,longitud"
        String coordenadas = latitud + "," + longitud;

        //copiar al portapapeles
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Coordenadas", coordenadas);
        clipboard.setPrimaryClip(clipData);

        //mensaje al usuario
        Toast.makeText(this, "Coordenadas copiadas: " + coordenadas, Toast.LENGTH_SHORT).show();
    }
}