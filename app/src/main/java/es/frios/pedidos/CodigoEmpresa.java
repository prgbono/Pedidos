package es.frios.pedidos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

//Clase Manager de obtención de datos de la BBDD
import static es.frios.pedidos.HttpManager.obtenerContenido;
import static es.frios.pedidos.HttpManager.getStringFromInputStream;


public class CodigoEmpresa extends Activity {
    //vbles globales
    //url Api MAC, movil real y genymotion

    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    private Empresa datosEmpresa;  //recibe los datos de la nueva empresa a crear

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_codigo_empresa);

        //Obtener los datos de la empresa informados en 'CrearEmpresa'
        //---------------------------------------------------------------------------------------------------------
        Intent i = getIntent();
        datosEmpresa = (Empresa)i.getSerializableExtra("datosE");  //En datosEmpresa tengo nombre, gerente y código de Empresa, además de los bool notificaciones y L/E

        //Llamada a la clase AsyncTask que insertará la E en la BBDD
        //---------------------------------------------------------------------------------------------------------
        InsertarE crearE = new InsertarE();
        crearE.execute(this);
        //---------------------------------------------------------------------------------------------------------

    }

    private class InsertarE extends AsyncTask<Context, Integer, String>{
        boolean empCreada=false;
        String empExiste, msg_error;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Cambiar el texto del primer TextView de la vista
            ((TextView)findViewById(R.id.txtMutableCodigoEmpresa)).setText(R.string.creandoE);

            //Ocultar todos los TextEdit de la vista hasta que se produzca la inserción
            findViewById(R.id.txtCodigoCodigoEmpresa).setVisibility(View.GONE);
            findViewById(R.id.txtFaciliteCodigoEmpresa).setVisibility(View.GONE);
            findViewById(R.id.txtModificarCodigoEmpresa).setVisibility(View.GONE);

            //Mostrar 'Loading'
            ((ProgressBar)findViewById(R.id.progressBarCodigoEmpresa)).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Context... params) {
            //Antes de hacer la inserción comprobar que no haya ya una empresa con el mismo nombre
            //Para versiones futuras se permitirán empresas con el mismo nombre pq puede ocurrir. Se comprobará esto de otra forma

            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                //.replace('e','a')

                Log.i("Codigoempresa","httppost: "+urlApi+"peticion=buscarE&nombre="+datosEmpresa.getNombre().replace(" ","%20"));
                //Sustituir los espacios por %20
                InputStream buscarE = obtenerContenido(urlApi+"peticion=buscarE&nombre="+datosEmpresa.getNombre().replace(" ","%20"));
                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                empExiste = getStringFromInputStream(buscarE);
                //Log.i("myapp",empExiste);

                if (empExiste.equals("-1")) {
                    //Empresa ya existente
                    //Log.i("myapp", "Ya existe la E");
                    msg_error = "El nombre de la empresa ya se encuentra registrado. Por favor, introduzca otro nombre.";

                }else{
                    //SE INSERTA la empresa
                    //Log.i("myapp", "Se inserta la E");
                    //tengo los datos de la E en datosE y en fecha

                    //HttpClient
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(urlApi+"peticion=crearEmpresa");

                    //Datos a enviar a PHP
                    ArrayList<NameValuePair> empresa = new ArrayList<>();
                    empresa.add(new BasicNameValuePair("nombre", datosEmpresa.getNombre()));
                    empresa.add(new BasicNameValuePair("codigo", datosEmpresa.getCodigo()));
                    empresa.add(new BasicNameValuePair("gerente", datosEmpresa.getGerente()));

                    //Calcular la fecha para incluirla en el campo fecha_creación de la tabla empresas
                    Calendar cal = Calendar.getInstance();
                    int dia = cal.get(Calendar.DAY_OF_MONTH);
                    int nummes = cal.get(Calendar.MONTH)+1;
                    int year = cal.get(Calendar.YEAR);
                    String mes = mes(nummes);
                    String fecha = dia+" "+mes+" "+year;
                    //Log.i("myapp", datosEmpresa.getNombre());
                    //Log.i("myapp", fecha);
                    empresa.add(new BasicNameValuePair("fecha_creacion", fecha));
                    //---------------------------------------------------------------------------------------------------------

                    //Logo, pte. Metemos un campo fake mientras solucionamos lo del logo
                    empresa.add(new BasicNameValuePair("logo", "LogoFake"));

                    empresa.add(new BasicNameValuePair("LE", String.valueOf(datosEmpresa.islE())));
                    empresa.add(new BasicNameValuePair("notificaciones", String.valueOf(datosEmpresa.isNotificaciones())));

                    //Insertar el paquete 'empresa' en la petición y enviarlo
                    try{
                        httpPost.setEntity(new UrlEncodedFormEntity(empresa));
                        httpClient.execute(httpPost);
                        empCreada=true;
                    }catch(Exception e){
                        msg_error= "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde";
                        //Toast.makeText(CodigoEmpresa.this, msg_error, Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(CodigoEmpresa.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (empCreada){
                //Cambiar el texto del primer TextView de la vista
                ((TextView)findViewById(R.id.txtMutableCodigoEmpresa)).setText(R.string.codEmpresa);

                //Ocultar el 'Loading'
                ((ProgressBar)findViewById(R.id.progressBarCodigoEmpresa)).setVisibility(View.GONE);

                //Mostrar todos los TextEdit de la vista. El del código debe cargar el código correspondiente de la Empresa
                //Traerte el código de la E mediante consulta SQL
                ((TextView)findViewById(R.id.txtCodigoCodigoEmpresa)).setText(datosEmpresa.getCodigo());
                findViewById(R.id.txtCodigoCodigoEmpresa).setVisibility(View.VISIBLE);

                findViewById(R.id.txtFaciliteCodigoEmpresa).setVisibility(View.VISIBLE);
                findViewById(R.id.txtModificarCodigoEmpresa).setVisibility(View.VISIBLE);
            }else{
                finish();
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
                Toast.makeText(CodigoEmpresa.this,msg_error, Toast.LENGTH_LONG).show();
            }

        }
    }

    public String mes(int nummes){
        switch (nummes){
            case 1: return "ENE";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "ABR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AGO";
            case 9: return "SEP";
            case 10: return "OCT";
            case 11: return "NOV";
            case 12: return "DIC";
            default: return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
    }

    public void back (View v){
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
    }



}








