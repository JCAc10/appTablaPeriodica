package com.example.proyectotablap;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {
    Button btnTest;
    TextView n1, ultimoP, txtMayorPuntaje;
    EditText punt;
    ImageView imgQR;
    public static final String nombres= "names";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        enlazarControles();
        System.out.println("ESTAS EN LOGIN");
    }

    private void enlazarControles() {
        btnTest = (Button) findViewById(R.id.btnTest);
        imgQR = (ImageView) findViewById(R.id.qrCode);
        n1 = (TextView) findViewById(R.id.txtUsuario);
        ultimoP = (TextView) findViewById(R.id.txtUltimoPuntaje);
        txtMayorPuntaje = (TextView) findViewById(R.id.txtMayorPuntaje);
        String usuario = getIntent().getStringExtra("names");
        mostrarPuntaje("nomUser=" + usuario + ""); /* Coloca el host de tu DB y de tu usuario */

        n1.setText(usuario);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(login.this, TablaPeriodica.class);
                i.putExtra(login.nombres, usuario);
                startActivity(i);

            }
        });
    }


    public void mostrarPuntaje(String URL) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try{
                        jsonObject = response.getJSONObject(i);
                        String puntajeUsuario = jsonObject.getString("ultimo_ptj");
                        ultimoP.setText(jsonObject.getString("ultimo_ptj"));
                        txtMayorPuntaje.setText(jsonObject.getString("ultimo_ptj"));
                        generarQR(puntajeUsuario);
                    }catch(JSONException e){
                        System.out.println("ERROR MOSTRAR PUNTAJE: "+e);
                        Toast.makeText(getApplicationContext(), "ERROR DE CONEXION ONRESPONSE: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR RESPONSE: " + error);
                         Toast.makeText(getApplicationContext(), "ERROR DE CONEXION RESPONSE", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void generarQR(String puntajeUsuario){
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap("Usuario: "+ n1.getText().toString() + " Puntaje: " + puntajeUsuario, BarcodeFormat.QR_CODE, 700, 700);
            imgQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
   /* public void mstPuntaje(String URL){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    punt.setText(response.getString("ultimo_ptj"));
                    txtMayorPuntaje.setText(response.getString("nomUser"));
                }catch(JSONException e){
                    System.out.println("ERROR MOSTRAR PUNTAJE: "+e);
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXION ONRESPONSE: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR RESPONSE: " + error);
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION RESPONSE", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }*/
   private void obtenerDatos(){
       SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
       boolean sesion = preferences.getBoolean("sesion", false);
       String usuario = getIntent().getStringExtra("names");
       if(sesion){
           n1.setText("");
       }
   }
}