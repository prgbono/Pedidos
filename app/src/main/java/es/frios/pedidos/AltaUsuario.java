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

//Clase Manager de obtención de datos de la BBDD
import static es.frios.pedidos.HttpManager.obtenerContenido;
import static es.frios.pedidos.HttpManager.getStringFromInputStream;


public class AltaUsuario extends Activity {
    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    private String id_E, nombreE, nombreU, apellidos, correo, pass, rpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alta_usuario);

        //Obtener el nombre y el ID de la empresa que vienen de la activity Anadirme
        //---------------------------------------------------------------------------------------------------------
        Intent i = getIntent();
        nombreE = i.getStringExtra("nombreE");
        id_E = i.getStringExtra("id_E");
        ((TextView)findViewById(R.id.txtEAltaUsuario)).setText(nombreE);
        //Log.i("myapp", "DEsde ALta Usuario: " + nombreE + "ID:" +id_E);
    }


    public void altaUsuario (View v){
        //Método para dar de alta un usuario en la Empresa tras pulsar el TIC de la parte superior derecha y tras haber validado el código introducido

        //Guardamos los datos del usuario
        nombreU = ((EditText)findViewById(R.id.editNombreAltaUsuario)).getText().toString();
        apellidos = ((EditText)findViewById(R.id.editApellidosAltaUsuario)).getText().toString();
        correo =((EditText)findViewById(R.id.editCorreoAltaUsuario)).getText().toString();
        pass =((EditText)findViewById(R.id.editPassAltaUsuario)).getText().toString();
        rpass =((EditText)findViewById(R.id.editRPassAltaUsuario)).getText().toString();

        /*COMPROBACIÓN  DE CAMPOS*/
        if ((nombreU.isEmpty()) || (pass.isEmpty()) || (rpass.isEmpty()) || !(pass.equals(rpass))){
          //Toast de campos de nombre y contraseñas obligatorios
            Toast.makeText(getBaseContext(), "Nombre y contraseña obligatorios. Contraseña debe coincidir.", Toast.LENGTH_LONG).show();
        }else{
            //Log.i("myapp", "AltaUsuario llama a la AsyncTask");
            //Llamada a la clase AsyncTask que insertará al usuario en la BBDD y con el id de la E correspondiente
            InsertarUsuario insertarUsuario = new InsertarUsuario();
            insertarUsuario.execute(this);
        }
    }


    private class InsertarUsuario extends AsyncTask<Context, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Cambiar el texto del TextView de la vista
            String mensaje = ((TextView)findViewById(R.id.txtLoadingAltaUsuario)).getText().toString()+" " + nombreE + "...";
            ((TextView)findViewById(R.id.txtLoadingAltaUsuario)).setText(mensaje);

            //Ocultar todos los TextEdit de la vista hasta que se produzca la inserción
            findViewById(R.id.editNombreAltaUsuario).setVisibility(View.GONE);
            findViewById(R.id.editApellidosAltaUsuario).setVisibility(View.GONE);
            findViewById(R.id.editCorreoAltaUsuario).setVisibility(View.GONE);
            findViewById(R.id.editPassAltaUsuario).setVisibility(View.GONE);
            findViewById(R.id.editRPassAltaUsuario).setVisibility(View.GONE);

            //Mostrar 'Loading'
            ((ProgressBar)findViewById(R.id.progressBarAltaUsuario)).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Context... params) {
            //Antes de hacer la inserción comprobar que no haya ya una empresa con el mismo nombre
            //Para versiones futuras se permitirán empresas con el mismo nombre pq puede ocurrir. Se comprobará esto de otra forma

            try {
                //HttpClient
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(urlApi+"peticion=altaUsuario");

                //Dar de alta al usuario y prepararlo para enviar a PHP
                ArrayList<NameValuePair> usuario = new ArrayList<>();
                usuario.add(new BasicNameValuePair("nombreU", nombreU));
                usuario.add(new BasicNameValuePair("apellidos", apellidos));
                usuario.add(new BasicNameValuePair("correo", correo));
                usuario.add(new BasicNameValuePair("id_E", id_E));
                usuario.add(new BasicNameValuePair("pass", pass));

                //Insertar el paquete 'usuario' en la petición y enviarlo
                try{
                    httpPost.setEntity(new UrlEncodedFormEntity(usuario));
                    httpClient.execute(httpPost);
                }catch(Exception e){
                    //msg_error= "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde";
                    Toast.makeText(AltaUsuario.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(AltaUsuario.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            finish();
            //Enviar a la pantalla inicial de la E
            Intent i = new Intent(AltaUsuario.this,MainActivity.class);
            startActivity(i);
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
