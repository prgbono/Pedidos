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
import android.widget.CheckBox;
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
import java.util.concurrent.ExecutionException;

import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import static es.frios.pedidos.HttpManager.obtenerContenido;
import es.frios.pedidos.Manager.InsertarPedido;
import es.frios.pedidos.Manager.ObtenerIdPedido;

public class Alta_Cliente extends Activity {
    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    int id_P=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alta__cliente);
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

    public void irNuevoPedido(View v) throws ExecutionException, InterruptedException{
        InsertarPedido insertarPedido = new InsertarPedido();
        insertarPedido.execute();

        ObtenerIdPedido obtenerIdPedido = new ObtenerIdPedido();
        id_P = Integer.valueOf(obtenerIdPedido.execute().get());

        //Tengo que extraer el id del pedido que acabo de insertar y enviárselo con putExtra a nuevopedido
        Intent i = new Intent(this, nuevopedido.class);
        i.putExtra("id_P", id_P);
        startActivity(i);
    }


    public void irListadoClientes(View v){
        Intent i = new Intent(this, Listado_Clientes.class);
        startActivity(i);
    }


    public void irExistencias(View v){
        Intent i = new Intent(this, Existencias.class);
        startActivity(i);
    }

    public void Anadir_cliente(View v){
        //Método para añadir cliente a la empresa en curso
        //Comprobamos que como mínimo exista nombre del cliente
        String nombre = ((EditText)findViewById(R.id.editNombreClienteAltaCLiente)).getText().toString();
        if (!(nombre.trim().equals(""))){
            InsertarCliente insertarCliente = new InsertarCliente();
            insertarCliente.execute(this);
        }
        else{
            Toast.makeText(Alta_Cliente.this, "Nombre del cliente obligatorio", Toast.LENGTH_SHORT).show();
        }


    }

    public void irListarPedidos(View v){
        Intent i = new Intent(this, ListarPedidos.class);
        startActivity(i);
    }

    private class InsertarCliente extends AsyncTask<Context, Integer, String> {
        String nombre, apellidos, telefono, correo, envio, ciudad;
        boolean bar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Obtener los datos de la empresa
            nombre = ((EditText)findViewById(R.id.editNombreClienteAltaCLiente)).getText().toString();
            apellidos = ((EditText)findViewById(R.id.editApellidosClienteAltaCliente)).getText().toString();
            telefono = ((EditText)findViewById(R.id.editTlfClienteAltaCliente)).getText().toString();
            correo = ((EditText)findViewById(R.id.editCorreoClienteAltaCliente)).getText().toString();
            envio = ((EditText)findViewById(R.id.editEnvioClienteAltaCliente)).getText().toString();
            bar = ((CheckBox)findViewById(R.id.checkBarAltaCliente)).isChecked();
            ciudad = ((EditText)findViewById(R.id.editCiudadClienteAltaCliente)).getText().toString();

            //Mostrar 'Loading'
            //((ProgressBar)findViewById(R.id.progressBarCodigoEmpresa)).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Context... params) {
            //Habría que comprobar que no existe ya el cliente?

            //generar un nuevo objeto cliente con id_E=1. El id_E se tendrá que calcular en la v2
            Cliente datosCliente = new Cliente(1,nombre, apellidos, telefono, correo, envio, bar, ciudad);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=crearCliente");

            //Datos a enviar a PHP
            ArrayList<NameValuePair> cliente = new ArrayList<>();
            cliente.add(new BasicNameValuePair("id_E", String.valueOf(datosCliente.getId_E())));
            cliente.add(new BasicNameValuePair("nombre", datosCliente.getNombre()));
            cliente.add(new BasicNameValuePair("apellidos", datosCliente.getApellidos()));
            cliente.add(new BasicNameValuePair("telefono", datosCliente.getTlf()));
            cliente.add(new BasicNameValuePair("correo", datosCliente.getCorreo()));
            cliente.add(new BasicNameValuePair("envio", datosCliente.getEnvio()));
            cliente.add(new BasicNameValuePair("bar", String.valueOf(datosCliente.isBar())));
            cliente.add(new BasicNameValuePair("ciudad", datosCliente.getCiudad()));

            //Insertar el paquete 'cliente' en la petición y enviarlo
            try{
                httpPost.setEntity(new UrlEncodedFormEntity(cliente));
                httpClient.execute(httpPost);

            }catch(Exception e){
                Toast.makeText(Alta_Cliente.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Ocultar el 'Loading'
            //((ProgressBar)findViewById(R.id.progressBarCodigoEmpresa)).setVisibility(View.GONE);
            Toast.makeText(Alta_Cliente.this,"Cliente creado", Toast.LENGTH_LONG).show();
            finish();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
        }
    }

}
