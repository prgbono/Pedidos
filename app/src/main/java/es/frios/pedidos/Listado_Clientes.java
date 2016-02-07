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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import static es.frios.pedidos.HttpManager.obtenerContenido;
import es.frios.pedidos.Manager.InsertarPedido;
import es.frios.pedidos.Manager.ObtenerIdPedido;


public class Listado_Clientes extends Activity {
    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    int id_P=0;

    //Array con objetos Clientes
    private ArrayList<Cliente> listaClientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listado__clientes);



        //Listado de clientes
        //-----------------------------------------------------------------------------------------------------------------//
        //limpiamos el array
        listaClientes.clear();

        //Obtenemos todos los clientes de la empresa. AsyncTask
        LClientes lClientes = new LClientes();
        lClientes.execute();

        //Montamos la lista con los pedidos obtenidos
        ClientesAdapter adapter = new ClientesAdapter(this, R.layout.row_cliente, listaClientes);
        ((ListView)findViewById(R.id.listClientes)).setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        //-----------------------------------------------------------------------------------------------------------------//


        //Evento onClick a la lista de clientes. Detalle de cada cliente
        //-----------------------------------------------------------------------------------------------------------------//
        ListView listadoClientes = (ListView)findViewById(R.id.listClientes);
        listadoClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtener cliente seleccionado
                Cliente clienteSeleccionado = listaClientes.get(position);
                //Pasar cliente con todos sus datos a la activity detalle
                Intent i = new Intent(Listado_Clientes.this, EditarCliente.class);
                i.putExtra("cliente", clienteSeleccionado);
                startActivity(i);
            }
        });
        //-----------------------------------------------------------------------------------------------------------------//

    }


    @Override
    protected void onResume(){
        super.onResume();

    }


    public void actualizar(View v){
        //limpiamos el array
        listaClientes.clear();

        //Obtenemos todos los clientes de la empresa. AsyncTask
        LClientes lClientes = new LClientes();
        lClientes.execute();

        //Montamos la lista con los pedidos obtenidos
        //ClientesAdapter adapter = new ClientesAdapter(this, R.layout.row_cliente, listaClientes);
        //((ListView)findViewById(R.id.listClientes)).setAdapter(adapter);
        //adapter.notifyDataSetChanged();
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


    public void irListarPedidos(View v){
        Intent i = new Intent(this, ListarPedidos.class);
        startActivity(i);
    }


    public void irExistencias(View v){
        Intent i = new Intent(this, Existencias.class);
        startActivity(i);
    }

    private class LClientes extends AsyncTask<Context, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar el loading
            //((ProgressBar) findViewById(R.id.progressBarListarPedidos)).setVisibility(View.VISIBLE);
            Toast.makeText(Listado_Clientes.this, "Obteniendo clientes...", Toast.LENGTH_SHORT).show();

        }


        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream clientes = obtenerContenido(urlApi+"peticion=listarClientes&id_E=1");

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                //Log.i("Listado_CLientes: ", "dwv: "+getStringFromInputStream(clientes));
                return getStringFromInputStream(clientes);
            } catch (Exception e) {
                Toast.makeText(Listado_Clientes.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("Listado_CLientes s1", s);
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("Listado_CLientes s2", s);
                int id_cliente;
                int id_E;
                String nombre, apellidos, telefono, correo, envio, ciudad, aux_bar;
                boolean bar;
                JSONArray json = new JSONArray(s);
                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    id_cliente = json_data.getInt("id");
                    id_E = json_data.getInt("id_E");
                    nombre = json_data.getString("nombre");
                    apellidos = json_data.getString("apellidos");
                    telefono = json_data.getString("telefono");
                    correo = json_data.getString("correo");
                    envio = json_data.getString("envio");

                    //bar = json_data.getBoolean("bar");
                    aux_bar = json_data.getString("bar");
                    if (aux_bar.equals("1")){bar = true;}else{bar=false;}

                    ciudad = json_data.getString("ciudad");

                    //Crear y almacenar el objeto

                    Cliente cli = new Cliente(id_E, nombre, apellidos, telefono, correo, envio, bar, ciudad);
                    listaClientes.add(cli);
                }
                //Al salir del bucle ya tenemos el array de objetos modelo. Montar la lista
                ClientesAdapter adapter = new ClientesAdapter(Listado_Clientes.this, R.layout.row_cliente, listaClientes);
                ((ListView)findViewById(R.id.listClientes)).setAdapter(adapter);

            }catch(JSONException e){
                Toast.makeText(Listado_Clientes.this,"Datos incorrectos1",Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            //((ProgressBar)findViewById(R.id.progressBarListarPedidos)).setVisibility(View.GONE);
            //finish();
        }
    }

}
