package es.frios.pedidos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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



public class ListarPedidos extends Activity {
    //Globales
    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    //Array con objetos Pedido
    private ArrayList<Pedido> listaPedidos = new ArrayList<>();

    //private String nombreCli; //nombre Cliente

    int id_P=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listar_pedidos);


        //limpiamos el array
        listaPedidos.clear();

        //Obtenemos todos los pedidos existentes de la empresa. AsyncTask
        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tarea asincrona para traer todas las E. En la v2 se traerán sólo aquellas a las que el usuario pertenezca
        LPedidos lPedidos = new LPedidos();
        lPedidos.execute();
        //------------------------------------------------------------------------------------------------------------------------------------//

        //Montamos la lista con los pedidos obtenidos
        PedidosAdapter adapter = new PedidosAdapter(this, R.layout.row_listapedidos, listaPedidos);
        ((ListView)findViewById(R.id.listListarPedidos)).setAdapter(adapter);
        //adapter.notifyDataSetChanged();



        //Evento onClick a la lista de pedidos. Detalle de cada pedido
        //-----------------------------------------------------------------------------------------------------------------//
        ListView listadoPedidos = (ListView)findViewById(R.id.listListarPedidos);
        listadoPedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtener pedido seleccionado
                Pedido pedidoSeleccionado = listaPedidos.get(position);
                //Pasar pedido con todos sus datos a la activity detalle
                Intent i = new Intent(ListarPedidos.this, DetallePedido.class);
                i.putExtra("pedido", pedidoSeleccionado);
                //i.putExtra("position", position);  //Le enviamos a detallePedido el cliente de este pedido
                startActivity(i);
            }
        });
        //-----------------------------------------------------------------------------------------------------------------//

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public void actualizar(View v){
        //limpiamos el array
        listaPedidos.clear();

        //Obtenemos todos los pedidos existentes de la empresa. AsyncTask
        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tarea asincrona para traer todas las E. En la v2 se traerán sólo aquellas a las que el usuario pertenezca
        LPedidos lPedidos = new LPedidos();
        lPedidos.execute();
        //------------------------------------------------------------------------------------------------------------------------------------//

        //Montamos la lista con los pedidos obtenidos
        PedidosAdapter adapter = new PedidosAdapter(this, R.layout.row_listapedidos, listaPedidos);
        ((ListView)findViewById(R.id.listListarPedidos)).setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    //AsyncTask listar los pedidos
    private class LPedidos extends AsyncTask<Context, Integer, String> {
        private String nombreCliente;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar el loading
            ((ProgressBar) findViewById(R.id.progressBarListarPedidos)).setVisibility(View.VISIBLE);
            Toast.makeText(ListarPedidos.this, "Obteniendo pedidos...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream pedidos = obtenerContenido(urlApi+"peticion=listarPedidos&id_E=1");

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                return getStringFromInputStream(pedidos);
            } catch (Exception e) {
                Toast.makeText(ListarPedidos.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("myapp", s);
                int id;
                int id_E;
                int id_Cli;
                double importe;
                String fecha_entrega, aux_bool_envio, aux_pagado, comentarios;
                boolean bool_envio, pagado;
                JSONArray json = new JSONArray(s);
                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    id = json_data.getInt("id");
                    id_E = json_data.getInt("id_E");
                    id_Cli = json_data.getInt("id_Cli");

                    //Con id_CLi e id_E tengo que hacer una nueva consulta para sacar el nombre del cliente!!
                    //------------------------------------------------------------------------------------------------------------------------------------//
                    //Tarea asincrona para traer todas las E. En la v2 se traerán sólo aquellas a las que el usuario pertenezca
                    ObtenerCli obtenerCli = new ObtenerCli(id_Cli);
                    nombreCliente = obtenerCli.execute().get();
                    //Log.i("myapp", "nombreCiente: "+nombreCliente);
                    //------------------------------------------------------------------------------------------------------------------------------------//
                    //Una vez obtenido el nombre del cliente continuamos extrayendo datos del pedido

                    fecha_entrega = json_data.getString("fecha_entrega");
                    //boolean pagado = json_data.getBoolean("pagado");
                    //boolean bool_envio = json_data.getBoolean("bool_envio");
                    aux_bool_envio = json_data.getString("bool_envio");
                    if (aux_bool_envio.equals("1")){bool_envio = true;}else{bool_envio=false;}
                    aux_pagado = json_data.getString("pagado");
                    if (aux_pagado.equals("1")){pagado = true;}else{pagado=false;}
                    comentarios = json_data.getString("comentarios");
                    importe = Double.valueOf(json_data.getString("importe"));
                    if (importe==0){
                        //Llamadas a dos AsyncTasks. La primera calcula el importe y la segunda nos lo devuelve para sobreescribir la vble importe
                        //Con id (id_P) hago el cálculo y el update del importe. AT.
                        //Con este if evitamos que se recalculen los importes de pedidos que ya están calculados, sólo entrará para los nuevos pedidos
                        CalcularImporte calcularImporte = new CalcularImporte(id);
                        calcularImporte.execute();

                        ObtenerImporte obtenerImporte = new ObtenerImporte(id);
                        importe = Double.valueOf(obtenerImporte.execute().get());
                    }

                    //Crear y almacenar el objeto
                    //Pedido ped = new Pedido(id, id_E, id_Cli, nombreCliente, fecha_entrega, Boolean.valueOf(bool_envio), comentarios, importe);
                    Pedido ped = new Pedido(id, id_E, id_Cli, nombreCliente, fecha_entrega, pagado, bool_envio, comentarios, importe);
                    listaPedidos.add(ped);
                }
                //Al salir del bucle ya tenemos el array de objetos modelo. Montar la lista

            }catch(JSONException e){
                Toast.makeText(ListarPedidos.this,"Datos incorrectos1",Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                Toast.makeText(ListarPedidos.this,"Datos incorrectos2",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(ListarPedidos.this,"Datos incorrectos3",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            //Ocultar el loading
            ((ProgressBar)findViewById(R.id.progressBarListarPedidos)).setVisibility(View.GONE);
            //finish();


        }
    }

    //AsyncTask para calcular el importe de cada pedido
    private class CalcularImporte extends AsyncTask<Context, Integer, String>{
        int id;
        private CalcularImporte(int id){this.id = id;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=updateImportePedido&id_P="+id);
            //Log.i("ListarPedidos","ListarPedidos: "+urlApi+"peticion=updateImportePedido&id_P"+id);
            try{
                httpClient.execute(httpPost);
            }catch(Exception e){
                Toast.makeText(ListarPedidos.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {super.onPostExecute(s);}
    }

    //AsyncTask para obtener el importe de cada pedido
    private class ObtenerImporte extends AsyncTask<Context, Integer, String>{
        int id;
        private ObtenerImporte(int id){this.id = id;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            String result;
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream nombreCat = obtenerContenido(urlApi+"peticion=obtenerImporte&id_P="+id);
                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                result=getStringFromInputStream(nombreCat);
                return result;
            } catch (Exception e) {
                Toast.makeText(ListarPedidos.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {super.onPostExecute(s);}
    }

    //AsyncTask para obtener el nombre del cliente de cada pedido
    private class ObtenerCli extends AsyncTask<Context, Integer, String> {
        int idCli ;
        private ObtenerCli(int id_Cli) {
            idCli = id_Cli;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream nombreCli = obtenerContenido(urlApi+"peticion=obtenerNombreCli&id_Cli="+idCli);
                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                return getStringFromInputStream(nombreCli);
            } catch (Exception e) {
                Toast.makeText(ListarPedidos.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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


    public void irExistencias(View v){
        Intent i = new Intent(this, Existencias.class);
        startActivity(i);
    }

}
