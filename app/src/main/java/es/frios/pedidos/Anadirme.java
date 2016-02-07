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
import android.widget.EditText;
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

import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import static es.frios.pedidos.HttpManager.obtenerContenido;


public class Anadirme extends Activity {
    //vbles globales
    //url Api MAC, movil real y genymotion

    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_anadirme);

    }


    public void validarUsuario (View v){
        //Método para validar el código introducido por el usuario para añadirse a una empresa tras pulsar el TIC de la parte superior derecha
        //En este método se hace la comprobación del código de empresa incluido por el usuario. Sólo tras las oportunas comprobaciones se le muestra el layout de alta de usuario 'AltaUsuario'

        //AsyncTask de comprobaciones, si las comprobaciones son correctas en el PostExecuted lanzar la activity AltaUsuario. Eoc Toast y no pasar de esta pantalla.
        //Llamada a la clase AsyncTask que comprobará el código de acceso de usuario a empresa
        //---------------------------------------------------------------------------------------------------------
        validarCodigo valCod = new validarCodigo();
        valCod.execute(this);
        //---------------------------------------------------------------------------------------------------------
    }


    private class validarCodigo extends AsyncTask<Context, Integer, String> {
        boolean valCodOk=false; //booleano que va a expresar si el código introducido es correcto o no
        String nombreE, id_E, codIntroducido;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Cambiar texto a "Validando código introducido..."
            ((TextView)findViewById(R.id.txtIntroduzcaAnadirme)).setText(R.string.validandoCodigo);
            //Ocultar el código de ejemplo y mostrar el loading
            findViewById(R.id.txtCodigoAnadirme).setVisibility(View.GONE);
            ((ProgressBar)findViewById(R.id.progressBarCodigoAnadirme)).setVisibility(View.VISIBLE);
            Log.i("myapp", "final de onPre");
        }

        @Override
        protected String doInBackground(Context... params) {
            try {

                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                //Log.i("myapp", ((EditText) findViewById(R.id.txtCodigoAnadirme)).getText().toString());
                codIntroducido= ((EditText)findViewById(R.id.txtCodigoAnadirme)).getText().toString();
                InputStream validarCodigo = obtenerContenido(urlApi+"peticion=validarCodigo&codigo="+codIntroducido.toString());

                Log.i("myapp", codIntroducido);

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                String codValido = getStringFromInputStream(validarCodigo);

                if (codValido.equals("1")) {
                    //Código introducido correcto
                    //Log.i("myapp", "Código OK");
                    valCodOk=true;
                        //Obtengo el nombre y el id de la E que voy a necesitar en la pantalla siguiente de Alta de usuario
                    InputStream obtenerNombreE = obtenerContenido(urlApi+"peticion=obtenerNombreE&codigo="+codIntroducido.toString());
                    InputStream obtenerId = obtenerContenido(urlApi+"peticion=obtenerId&codigo="+codIntroducido.toString());

                    //REtornar el nombre de la E en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                    nombreE = getStringFromInputStream(obtenerNombreE);
                    id_E = getStringFromInputStream(obtenerId);
                    Log.i("myapp", "Desde Anadirme: "+nombreE);
                }

            }catch (Exception e) {
                Toast.makeText(Anadirme.this,"Error de conexión", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //En cualquier caso se oculta el loading
            ((ProgressBar)findViewById(R.id.progressBarCodigoAnadirme)).setVisibility(View.GONE);

            if (valCodOk){
                //Lanzar la activity de altaUsuario
                Intent i = new Intent(Anadirme.this, AltaUsuario.class);
                i.putExtra("nombreE", nombreE);
                i.putExtra("id_E", id_E);
                startActivity(i);
                finish();
            }else{
                //limpiar campos de texto
                ((TextView)findViewById(R.id.txtIntroduzcaAnadirme)).setText(R.string.introduzcaCodigo);
                findViewById(R.id.txtCodigoAnadirme).setVisibility(View.VISIBLE);
                //Para volver a ponerle el hint al editText no puede tener texto por lo ue hay que pasarle null antes
                ((EditText)findViewById(R.id.txtCodigoAnadirme)).setText(null);
                ((EditText)findViewById(R.id.txtCodigoAnadirme)).setHint(R.string.codigoFicticio);
                //Mensaje de código introducido no válido
                Toast.makeText(Anadirme.this,"código incorrecto", Toast.LENGTH_LONG).show();
            }
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
