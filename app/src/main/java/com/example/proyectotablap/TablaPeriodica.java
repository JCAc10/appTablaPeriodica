package com.example.proyectotablap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablaPeriodica extends AppCompatActivity {
Button btnRetroceder, btnSiguiente, btnSalir;
RadioButton respA, respB, respC, respD;
TextView preg, numpreg;
ImageView cargarImagen;
int cont = 1;
double notaFinal = 0.00;
boolean check = false;
public static final String nombres= "names";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabla_periodica);
        enlazarControles();
    }

    private void enlazarControles() {
        btnRetroceder = (Button) findViewById(R.id.btnRegresar);
        btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
        btnSalir = (Button) findViewById(R.id.btnSalir);
        respA = (RadioButton) findViewById(R.id.respA);
        respB = (RadioButton) findViewById(R.id.respB);
        respC = (RadioButton) findViewById(R.id.respC);
        respD = (RadioButton) findViewById(R.id.respD);
        preg = (TextView) findViewById(R.id.txtPregunta);
        numpreg = (TextView) findViewById(R.id.txtNumPreg);
        cargarImagen = (ImageView) findViewById(R.id.imgTP);
        llenarResp(cont);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertarPuntaje("/puntaje.php"); /* Direccion del host de la DB y tu puntaje */
            }
        });
        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(TablaPeriodica.this, login.class);
                String usuario = getIntent().getStringExtra("names");
                i.putExtra(login.nombres, usuario);
                startActivity(i);

            }
        });
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(respA.isChecked()){
                    verificarRespuesta(cont, respA.getText().toString());
                    check = true;
                } else if(respB.isChecked()) {
                    verificarRespuesta(cont, respB.getText().toString());
                    check = true;
                } else if(respC.isChecked()) {
                    verificarRespuesta(cont, respC.getText().toString());
                    check = true;
                } else if(respD.isChecked()) {
                    verificarRespuesta(cont, respD.getText().toString());
                    check = true;
                }else{
                   Toast toast = Toast.makeText(getApplicationContext(),
                            "Marque una opcion",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                if(check){
                    if (cont < 10){
                        cont = cont + 1;
                        llenarResp(cont);
                    }else{
                        mostrarResultado();
                    }

                }
            }
        });
    }

    private void llenarResp(int numPreg) {
        String jsonFileContent = null;
        try {
            jsonFileContent = Conexion.llamarJson(getApplicationContext(), "preguntas.json");
            JSONObject jsonobject = new JSONObject(jsonFileContent);
            JSONArray jsonarray = jsonobject.getJSONArray("arrayPreguntas");

            for (int i = 0; i < jsonarray.length() ; i++) {
                JSONObject jsonobjc = jsonarray.getJSONObject(i);

                int id_preg = jsonobjc.getInt("id_Pregunta");
                System.out.println("ID: "+ id_preg+ " - numPreg: " + numPreg);
                if(id_preg == numPreg){
                    String pregunta = jsonobjc.getString("Pregunta");
                    String respuesta = jsonobjc.getString("respuesta");
                    String opcionA = jsonobjc.getString("opcion1");
                    String opcionB = jsonobjc.getString("opcion2");
                    String opcionC = jsonobjc.getString("opcion3");
                    String url = jsonobjc.getString("url");

                    preg.setText("Seleccione: Elemento y Número Atómico");
                    numpreg.setText("Pregunta N° " + id_preg + " ");
                    Picasso.get()
                            .load(url)
                            .error(R.mipmap.ic_launcher_round)
                            .into(cargarImagen);
                    List<String> opciones = Arrays.asList(respuesta, opcionA, opcionB, opcionC);

                    Collections.shuffle(opciones);

                    respA.setText(opciones.get(0));
                    respB.setText(opciones.get(1));
                    respC.setText(opciones.get(2));
                    respD.setText(opciones.get(3));
                }
            }
        }catch (IOException | JSONException e){
            System.out.println("PROBLEMA: "+e);
        }
    }

    public void verificarRespuesta(int numPreg, String respUs){
        String jsonFileContent = null;
        try {
            jsonFileContent = Conexion.llamarJson(getApplicationContext(), "preguntas.json");
            JSONObject jsonobject = new JSONObject(jsonFileContent);
            JSONArray jsonarray = jsonobject.getJSONArray("arrayPreguntas");
            for (int i = 0; i < jsonarray.length() ; i++) {
                JSONObject jsonobjc = jsonarray.getJSONObject(i);

                int id_preg = jsonobjc.getInt("id_Pregunta");

                if(id_preg == numPreg){
                    String respuesta = jsonobjc.getString("respuesta");
                    if(respuesta.equals(respUs)){
                        notaFinal= notaFinal + 2;
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Correcto",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    } else{
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Incorrecto",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        }catch (IOException | JSONException e){
            System.out.println("PROBLEMA: "+e);
        }
    }

    public void mostrarResultado(){
            numpreg.setText("Puntaje Final: " + notaFinal);
            if(notaFinal >= 6){
                preg.setTextColor(getResources().getColor(R.color.green));
                preg.setText("APROBADO");
            }else{
                preg.setTextColor(getResources().getColor(R.color.red));
                preg.setText("DESAPROBADO");
            }
            respA.setVisibility(View.INVISIBLE);
            respB.setVisibility(View.INVISIBLE);
            respC.setVisibility(View.INVISIBLE);
            respD.setVisibility(View.INVISIBLE);
            btnSiguiente.setVisibility(View.INVISIBLE);
        Picasso.get()
                .load("https://cdn-icons-png.flaticon.com/512/1505/1505465.png")
                .error(R.mipmap.ic_launcher_round)
                .into(cargarImagen);
    }

    public void insertarPuntaje(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    String usuario = getIntent().getStringExtra("names");
                    intent.putExtra(login.nombres, usuario);
                    startActivity(intent);
                   // finish();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Datos ingresados",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Datos no válidos",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TablaPeriodica.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String usuario = getIntent().getStringExtra("names");
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", usuario);
                parametros.put("puntaje", String.valueOf(notaFinal));
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}