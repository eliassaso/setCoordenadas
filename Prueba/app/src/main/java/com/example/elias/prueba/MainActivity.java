package com.example.elias.prueba;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.HttpResponseCache;

import android.content.Context;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText pass;
    EditText email;
    TextView response1;
    Button OKBu;
    TextView txtLatitud;
    TextView txtLongitud;
    HttpURLConnection urlConection = null;
    public String longitudGlobal = "";
    public String latitudGlobal = "";
    public String lugar = "";



    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtLatitud = (TextView)findViewById(R.id.txtLatitud);
        txtLongitud = (TextView)findViewById(R.id.txtLongitud);





          /* Uso de la clase LocationManager para obtener la localizacion del GPS */
        final   LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Localizacion Local = new  Localizacion();
        Local.setMainActivity(this);

	try {
          mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
	}catch(SecurityException e){
}

        /*final Runnable tarea = new Runnable() {
            public void run() {
                try {
                    setLocation(mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                }catch (SecurityException e){
                    //getApplication();
                }


            }
        };
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(tarea, 1, 20, TimeUnit.SECONDS);*/

        //gps();


    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //txtLongitud.setText("Mi direccion es: \n"
                      //      + DirCalle.getAddressLine(0));

                    latitudGlobal = String.valueOf(loc.getLatitude());
                    longitudGlobal = String.valueOf(loc.getLongitude());
                    lugar = DirCalle.getAddressLine(0);

                    Thread nt = new Thread() {
                        @Override
                        public void run() {

                            try {
                                final String res;

                                HttpClient httpClient = new DefaultHttpClient();
                                HttpContext localContext = new BasicHttpContext();
                                //HttpPost httpPost = new HttpPost("http://192.168.1.5:80/dbHandlerMerFilt/serverBusesHostinger.php");//http://escuelacr.esy.es/serverBusesHostinger.php
                                HttpPost httpPost = new HttpPost("http://escuelacr.esy.es/serverBusesHostinger.php");

                                HttpResponse response = null;
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String fecha = dateFormat.format(new java.util.Date()); // Find todays date
                                    List<NameValuePair> params = new ArrayList<NameValuePair>(4);
                                    params.add(new BasicNameValuePair("id", "1"));
                                    params.add(new BasicNameValuePair("latitud", latitudGlobal));
                                    params.add(new BasicNameValuePair("longitud", longitudGlobal));
                                    params.add(new BasicNameValuePair("lugar", lugar));
                                    params.add(new BasicNameValuePair("fecha", fecha));

                                    httpPost.setEntity(new UrlEncodedFormEntity(params));

                                    response = httpClient.execute(httpPost, localContext);
                                    HttpEntity entity = response.getEntity();
                                    String resultado = EntityUtils.toString(entity, "UTF-8");


                                } catch (Exception e) {
                                    // TODO: handle exception
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                    }
                                });


                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                    };
                    nt.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {

    }


    public class Localizacion implements LocationListener {
        MainActivity MainActivity;


        public MainActivity getMainActivity() {
            return MainActivity;
        }

        public void setMainActivity(MainActivity MainActivity) {
            this.MainActivity = MainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();
            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            //txtLatitud.setText(Text);
            this.MainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            txtLatitud.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            txtLatitud.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este metodo se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localizacion (GPS)
            // Los diferentes Status son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

    }/* Fin de la clase localizacion */

}




