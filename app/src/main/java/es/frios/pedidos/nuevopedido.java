package es.frios.pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

//import static es.frios.pedidos.HttpManager.calcularImporte;
import static es.frios.pedidos.HttpManager.obtenerContenido;
import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import es.frios.pedidos.Manager.InsertarPedido;
import es.frios.pedidos.Manager.ObtenerIdPedido;



public class nuevopedido extends Activity {

    //Vbles globales
    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    //Array con objetos modelo
    ArrayList<Pedido> listaPedidos = new ArrayList<>();
    ArrayList<Cliente> listaClientes = new ArrayList<>();
    ArrayList<Categoria> listaCategorias = new ArrayList<>();
    ArrayList<Especialidad> listaEspecialidades = new ArrayList<>();


    int id_Cat=1; //id de categoría que obtenemos en una AsyncTask y usamos en otra. En la primera llamada valdrá 1 pero después se reclacula. Mejorar esto en la v2
    Especialidad especialidadSeleccionada;  //Especialidad seleccionada en la lista (spinner)
    int id_Cli;  //id de cliente
    int id_P; //id del pedido
    double kg=0;
    boolean cocido=false;
    //boolean enableAnadirPedido=false; //Este bool controlará que sólo se pueda meter el pedido una vez en la tabla de pedidos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nuevopedido);

        //Obtener el id del pedido creado
        Intent i = getIntent();
        id_P = i.getIntExtra("id_P", id_P);

        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tareas asincronas para traer todos los clientes, categorías y especialidades existentes en la E para cargar los spinners.
        ListarClientes listarClientes = new ListarClientes();
        listarClientes.execute();

        ListarCategorias listarCategorias = new ListarCategorias();
        listarCategorias.execute();

        try {
            ListarEspecialidades listarEspecialidades = new ListarEspecialidades(id_Cat);
            listarEspecialidades.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------------------------------------------------//

        //limpiamos el array
        listaPedidos.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Aquí es donde, para la v2 se debe calcular el ID de la empresa!!!!

    }

    public void actualizar(View v){
        //Obtener el id del pedido creado
        Intent i = getIntent();
        id_P = i.getIntExtra("id_P", id_P);


        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tareas asincronas para traer todos los clientes, categorías y especialidades existentes en la E para cargar los spinners.
        ListarClientes listarClientes = new ListarClientes();
        listarClientes.execute();

        ListarCategorias listarCategorias = new ListarCategorias();
        listarCategorias.execute();

        try {
            ListarEspecialidades listarEspecialidades = new ListarEspecialidades(id_Cat);
            listarEspecialidades.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------------------------------------------------//

        //limpiamos el array
        listaPedidos.clear();
    }

    private class ListarClientes extends AsyncTask<Context, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream clientes = obtenerContenido(urlApi+"peticion=obtenerClientes&id_E=1"); //para la v2 calcular id_E

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                //String h= getStringFromInputStream(clientes);
                //Log.i("myapp", h);
                return getStringFromInputStream(clientes);
            } catch (Exception e) {
                Toast.makeText(nuevopedido.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("myapp", s);
                listaClientes.clear();
                JSONArray json = new JSONArray(s);
                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    id_Cli = json_data.getInt("id");  //id cliente
                    int id_E = json_data.getInt("id_E");  //id E
                    String nombreCli = (String) json_data.get("nombre");  //Nombre cliente
                    String apellidosCli = (String) json_data.get("apellidos");  //Nombre cliente
                    String tlfCli = (String) json_data.get("telefono");
                    String correo = (String) json_data.get("correo");
                    String envio = (String) json_data.get("envio");
                    int bar = json_data.getInt("bar");
                    //Paso a boolean la vble bar
                    boolean esBar;
                    if (bar==1) esBar = true;
                    else esBar = false;
                    String ciudad = (String) json_data.get("ciudad");

                    //Crear y almacenar el objeto
                    Cliente cli = new Cliente(nombreCli, apellidosCli, correo, envio, ciudad, tlfCli, id_Cli, id_E, esBar);
                    listaClientes.add(cli);
                }
                //Al salir del bucle ya tenemos el array de objetos modelo. Montar la lista

            }catch(JSONException e){
                Toast.makeText(nuevopedido.this,"Datos incorrectos1",Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            //((ProgressBar)findViewById(R.id.progressBarSelectorE)).setVisibility(View.GONE);
            //finish();

            //------------------------------------------------------------------------------------------------------------------------------------//
            //Cargamos el spinner
            //spinnerE = getResources().getStringArray(R.array.spinners);
            Spinner spinnerCli = (Spinner)findViewById(R.id.spinnerClienteNuevoPedido);
            SpinnerClientesAdapter adapter = new SpinnerClientesAdapter(nuevopedido.this, R.layout.row_spinnercli, listaClientes);
            //Apariencia DropDown
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCli.setAdapter(adapter);
            //Con el listener tenemos que coger el id de la E seleccionada
            spinnerCli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int i = parent.getSelectedItemPosition();
                    //Obtener categoria seleccionada
                    Cliente clienteSeleccionado = listaClientes.get(position);
                    id_Cli=clienteSeleccionado.getId();
                    Log.i("myapp", "Entra en onItemClickListener del spinner de clientes. id_Cli: " + id_Cli);

                    //Hay que habilitar la opción de que se pueda cambiar el cliente y generar un pedido nuevo. Para hacer esto habría que crear un uevo pedido y limpiar la lista de artículos añadidos
                    //V2

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //---------------------------------------------------------------------------------------------------//


        }
    }

    private class ListarCategorias extends AsyncTask<Context, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream categorias = obtenerContenido(urlApi+"peticion=obtenerCat&id_E=1");

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                return getStringFromInputStream(categorias);
            } catch (Exception e) {
                Toast.makeText(nuevopedido.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("myapp", s);
                listaCategorias.clear();
                JSONArray json = new JSONArray(s);
                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    id_Cat = json_data.getInt("id");  //id categoría
                    String nombreCat = (String) json_data.get("nombre");  //Nombre categoría
                    int id_E = json_data.getInt("id_E");  //id E de esa categoría

                    //Crear y almacenar el objeto
                    Categoria cat = new Categoria(id_Cat, nombreCat, id_E);
                    listaCategorias.add(cat);
                }
                //Al salir del bucle ya tenemos el array de objetos modelo. Montar la lista

            }catch(JSONException e){
                Toast.makeText(nuevopedido.this,"Datos incorrectos2",Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            //((ProgressBar)findViewById(R.id.progressBarSelectorE)).setVisibility(View.GONE);
            //finish();

            //------------------------------------------------------------------------------------------------------------------------------------//
            //Cargamos el spinner
            Spinner spinnerCat = (Spinner)findViewById(R.id.spinnerCategoriaNuevoPedido);
            SpinnerCategoriasAdapter adapter = new SpinnerCategoriasAdapter(nuevopedido.this, R.layout.row_spinnercat, listaCategorias);
            //Apariencia DropDown
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCat.setAdapter(adapter);
            //Con el listener tenemos que coger el id de la E seleccionada
            spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int i = parent.getSelectedItemPosition();
                    //Obtener categoria seleccionada
                    Categoria categoriaSeleccionada = listaCategorias.get(position);
                    id_Cat=categoriaSeleccionada.getId();
                    Log.i("myapp", "Entra en onItemClickListener del spinner de categorías. id_Cat: " + id_Cat);
                    try {
                        ListarEspecialidades listarEspecialidades = new ListarEspecialidades(id_Cat);
                        listarEspecialidades.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //---------------------------------------------------------------------------------------------------//


        }
    }

    private class ListarEspecialidades extends AsyncTask<Context, Integer, String> {

        int idCat ;
        private ListarEspecialidades(int id_Cat) {
            idCat = id_Cat;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                Log.i("myapp","id_Cat: "+id_Cat);
                InputStream especialidades = obtenerContenido(urlApi+"peticion=obtenerEsp&id_Cat="+id_Cat);

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                return getStringFromInputStream(especialidades);
            } catch (Exception e) {
                Toast.makeText(nuevopedido.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("myapp", s);
                listaEspecialidades.clear();
                JSONArray json = new JSONArray(s);
                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    int id = json_data.getInt("id_Esp");  //id especialidad
                    int idCategoria = json_data.getInt("id_Cat");  //id categoria de esa especialidad
                    String especialidad = (String) json_data.get("especialidad");  //Nombre especialidad
                    double pvp = json_data.getInt("pvp");
                    double existencias = json_data.getInt("existencias");

                    //Crear y almacenar el objeto
                    Especialidad esp = new Especialidad(id, idCategoria, especialidad, pvp, existencias);
                    listaEspecialidades.add(esp);
                }
                //Al salir del bucle ya tenemos el array de objetos modelo. Montar la lista

            }catch(JSONException e){
                Toast.makeText(nuevopedido.this,"Datos incorrectos3",Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            //((ProgressBar)findViewById(R.id.progressBarSelectorE)).setVisibility(View.GONE);
            //finish();

            //------------------------------------------------------------------------------------------------------------------------------------//
            //Cargamos el spinner
            Spinner spinnerEsp = (Spinner)findViewById(R.id.spinnerEspecialidadNuevoPedido);
            SpinnerEspecialidadesAdapter adapter = new SpinnerEspecialidadesAdapter(nuevopedido.this, R.layout.row_spinneresp, listaEspecialidades);
            //Apariencia DropDown
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEsp.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            //Con el listener tenemos que coger el id de la E seleccionada
            spinnerEsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int i = parent.getSelectedItemPosition();
                    //Obtener especialidad seleccionada y cargar los datos de pvp y existencias
                    especialidadSeleccionada = listaEspecialidades.get(position);
                    ((EditText) findViewById(R.id.editPvpNuevoPedido)).setText(Double.toString(especialidadSeleccionada.getPvp()));
                    ((EditText) findViewById(R.id.editExistenciasNuevoPedido)).setText(Double.toString(especialidadSeleccionada.getExistencias()));
                    Log.i("myapp", "Entra en onItemClickListener del spinner de especialidades. Esp seleccionada: "+especialidadSeleccionada.getId() +" pvp:"+especialidadSeleccionada.getPvp());
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //---------------------------------------------------------------------------------------------------//
        }
    }

    private class AnadirDetallePedido extends AsyncTask<Context, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            //Log.i("myapp", "en doInBK de AnadirPedido");

            String fecha_detalle = calcularFechaHora();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=insertarDetallePedido&id_E=1");

            //Preparamos datos a enviar a PHP
            ArrayList<NameValuePair> detallePedido = new ArrayList<>();
            detallePedido.add(new BasicNameValuePair("id_Cat", String.valueOf(id_Cat)));
            detallePedido.add(new BasicNameValuePair("id_Esp", String.valueOf(especialidadSeleccionada.getId())));
            detallePedido.add(new BasicNameValuePair("kg", String.valueOf(kg)));
            detallePedido.add(new BasicNameValuePair("cocido", String.valueOf(cocido)));
            detallePedido.add(new BasicNameValuePair("fecha_detalle", fecha_detalle));
            detallePedido.add(new BasicNameValuePair("pvp", String.valueOf(especialidadSeleccionada.getPvp())));

            //Log.i("myapp","idCat: "+String.valueOf(id_Cat)+"   idEsp: "+String.valueOf(especialidadSeleccionada.getId())+"   Kg: "+String.valueOf(kg)+
            //        "   cocido: "+String.valueOf(cocido)+ "   fecha detalle: " +fecha_detalle+ "   pvp :"+String.valueOf(especialidadSeleccionada.getPvp()));

            try{
                httpPost.setEntity(new UrlEncodedFormEntity(detallePedido));
                httpClient.execute(httpPost);

                //--------------------------------------------------------------------------------------------------------
                //La comprobación de que se ha insertado será que aparezca en la lista inferior de la activity
                //--------------------------------------------------------------------------------------------------------

            }catch(Exception e){
                Toast.makeText(nuevopedido.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }

            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //HttpClient httpClient = new DefaultHttpClient();
            //HttpPost httpPost = new HttpPost(urlApi+"peticion=obtenerIdPedido&id_E=1"); //para la v2 calcular id_E.

            //Ocultar el loading
            //finish();
        }
    }

    private class LPedidoAnadido extends AsyncTask<Context, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar el loading. HAY que poner un ProgressBar en la vista
            //((ProgressBar) findViewById(R.id.progressBarListarPedidos)).setVisibility(View.VISIBLE);
            //Toast.makeText(ListarPedidos.this, "Obteniendo pedidos...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream detallePedido = obtenerContenido(urlApi+"peticion=listarAnadidos&id_Cat="+especialidadSeleccionada.getId_Cat()+"&id_Esp="+especialidadSeleccionada.getId()+"&id_P="+id_P);
                Log.i("nuevopedido","httppost: "+urlApi+"peticion=listarAnadidos&id_Cat="+especialidadSeleccionada.getId_Cat()+"&id_Esp="+especialidadSeleccionada.getId()+"&id_P="+id_P);
                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                return getStringFromInputStream(detallePedido);
            } catch (Exception e) {
                Toast.makeText(nuevopedido.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("nuevopedido", s);
                int id_detalle, id_Pedido;
                String cat, esp;
                double pvp, kg;
                JSONArray json = new JSONArray(s);

                //listaPedidos.clear(); //si limpio el array al cambiar de categoría no guarda los artículos de otras categorías

                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    id_detalle = json_data.getInt("id");
                    id_Pedido = json_data.getInt("id_Pedido");
                    cat = json_data.getString("cat");
                    esp = json_data.getString("esp");
                    pvp  = Double.valueOf(BigDecimal.valueOf(json_data.getDouble("pvp")).toPlainString());
                    kg  = Double.valueOf(BigDecimal.valueOf(json_data.getDouble("kg")).toPlainString());
                    //double pvpAcordado  = Double.valueOf(BigDecimal.valueOf(json_data.getDouble("pvp_acordado")).toPlainString());

                    Log.i("myapp","Cat: "+cat+"  esp: "+esp+"  pvp: "+pvp+"  kg: "+kg);

                    //Crear y almacenar el objeto
                    Pedido vistaPedido = new Pedido(id_detalle, id_Pedido, cat, esp, pvp, kg);
                    listaPedidos.add(vistaPedido);
                }

                //Al salir del bucle ya tenemos el array de objetos modelo.
                //Borrar de listaPedidos los artículos con mismo id_detalle para que no se repitan en el spinner (Ocurrirá cd se añadan dos artículos con mismo esp y cat. Para ello usamos un array auxiliar
                ArrayList<Pedido> listaPedidos_aux = new ArrayList<>();
                ArrayList<Integer> repetidos = new ArrayList<>();
                int aux;
                for (int i=0;i<listaPedidos.size();i++) {
                    aux = listaPedidos.get(i).getId_detalle();
                    if (!repetidos.contains(aux)){
                        repetidos.add(listaPedidos.get(i).getId_detalle());
                        listaPedidos_aux.add(listaPedidos.get(i));
                    }
                }

                NuevoPedidoAdapter adapter = new NuevoPedidoAdapter(nuevopedido.this, R.layout.row_nuevopedido, listaPedidos_aux);
                ((ListView)findViewById(R.id.listNuevoPedido)).setAdapter(adapter);

            }catch(JSONException e) {
                Toast.makeText(nuevopedido.this, "Datos incorrectos4", Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            //((ProgressBar)findViewById(R.id.progressBarListarPedidos)).setVisibility(View.GONE);
            //finish();


        }
    }

    private class UpdateExistencias extends AsyncTask<Context, Integer, String>{
        //vbles
        //int id_Categoria, id_Especialidad;
        double kg, existencias;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Captar datos de empresa, categoría, especialidad, pvp, precio coste, kg y existencias introducidos por el usuario
            //id_Categoria = ((Categoria)((Spinner)findViewById(R.id.spinnerClienteNuevoPedido)).getSelectedItem()).getId();  // tb se podría obtener como id_Categoria = especialidadSeleccionada.getId_Cat();
            //id_Especialidad = especialidadSeleccionada.getId();
            //pvp = Double.valueOf(((EditText)findViewById(R.id.editPvpExistencias)).getText().toString());
            //pvp_coste = Double.valueOf(((EditText)findViewById(R.id.editPCosteExistencias)).getText().toString());
            kg = Double.valueOf(((EditText)findViewById(R.id.editKgNuevoPedido)).getText().toString());
            //existencias = Double.valueOf(((EditText)findViewById(R.id.editExistenciasExistencias)).getText().toString());
            //Log.i("Existencias","id_Categoria:"+id_Categoria+", id_Especialidad:"+id_Especialidad+", pvp:"+pvp+", pvp_Coste:"+pvp_coste+", kg:"+kg+", existencias:"+existencias);

        }


        @Override
        protected String doInBackground(Context... params) {
            //Añadir los kilos introducidos a las existencias existentes de la especialidad seleccionada
            //Restar los kg puestos en el artículo del pedido
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=updateExistenciasTrasAnadirArticulo&id_Esp="+especialidadSeleccionada.getId()+"&existencias="+(especialidadSeleccionada.getExistencias()-kg));
            try{
                httpClient.execute(httpPost);
            }catch(Exception e){
                Toast.makeText(nuevopedido.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class UpdateClientePedido extends AsyncTask<Context, Integer, String>{
        //Dividimos el update de pedido en dos AT. Una para obtener el id del pedido y otra para la actualización del pedido en sí
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            String result;
            //obtener el id del pedido que hay q actualizar
            try {
                InputStream id_Cli = obtenerContenido(urlApi+"peticion=obtenerIdPedido&id_E=1");
                result=getStringFromInputStream(id_Cli);
                return result;

            } catch (Exception e) {
                Toast.makeText(nuevopedido.this,"Error de conexión", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //AT para hacer el Update del pedido para añadir el cliente correspondiente
            id_P = Integer.valueOf(s);
            UpdateClientePedidoAux updateClientePedidoAux = new UpdateClientePedidoAux(Integer.valueOf(s));
            updateClientePedidoAux.execute();
        }
    }

    private class UpdateClientePedidoAux extends AsyncTask<Context, Integer, String>{
    int id_P;
        public UpdateClientePedidoAux(int s)
        {
            this.id_P = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=updateClientePedido&id_E=1&id_Cli="+id_Cli+"&id_P="+this.id_P); //s es el id_P
            //Log.i("nuevopedido", "httppost: "+urlApi+"peticion=updateClientePedido&id_E=1&id_Cli="+id_Cli+"&id_P="+Integer.valueOf(s));
            try{
                httpClient.execute(httpPost);
            }catch(Exception e){
                Toast.makeText(nuevopedido.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public String calcularFechaHora(){
        //Asignamos al textView de la fecha la fecha Actual
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int nummes = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        //Formateamos la hora para el estandar HH:MM
        String hora_formateada = String.format("%02d:%02d", hora, min);
        String mes = mes(nummes);
        return (dia+" "+mes+" "+year+" "+hora_formateada+"h");

    }

    public String calcularFecha(){
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int nummes = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        String mes = mes(nummes);
        return (dia+"-"+mes+"-"+year);

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

    public void anadir (View v){
        /*
        Método para añadir un producto a un pedido. Pasos:
        1. Recoger los datos introducidos por el usuario
        2. Añadir esos datos (artículos) a la tabla detalle_pedido con el id del pedido insertado al pulsar sobre 'Nuevo Pedido' de la pantalla inicial
            2.1 Recuperar el id del pedido recien insertado
            2.2 Insertar en detalle_pedido con el id de pedido recuperado anteriormente
        3.Actualizar el pedido insertado al pulsar sobre NuevoPdido en la pantalla inicial para incluirle el id del cliente correspondiente
        4. Mostrar el nuevo artículo añadido en la lista inferior de la activity
        5. Actualizar existencias tras añadir el artículo
        6. Deshabilitar el spinner de clientes para no poder cambiar de cliente. Para la V2 esto se podrá hacer pero habrá que generar un nuevo pedido y limpiar la lista de artículos
         */

        //1
        //Cargar los datos que vendran de la tabla de especialidades (pvp y existencias)

        if ((((EditText)findViewById(R.id.editKgNuevoPedido)).getText().toString()).isEmpty()){
            Toast.makeText(nuevopedido.this,"Necesario número de Kgs", Toast.LENGTH_SHORT).show();
        }else{
            //Comprobamos la disponibilidad de existencias if (kg introducidos > existencias)
            if (Double.valueOf((((EditText)findViewById(R.id.editKgNuevoPedido)).getText().toString())) > especialidadSeleccionada.getExistencias()){
                Toast.makeText(nuevopedido.this,"Existencias insuficientes", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.i("nuevopedido", "Existencias: " + especialidadSeleccionada.getExistencias());
                kg = Double.valueOf((((EditText) findViewById(R.id.editKgNuevoPedido)).getText().toString()));
                //Quitar PvpAcordado de la vista. Se retomará en la v2
                //pvpAcordado = Double.valueOf((((EditText)findViewById(R.id.editPvpAcordadoNuevoPedido)).getText().toString()));
                //pvpAcordado=0;
                cocido = Boolean.valueOf((((CheckBox) findViewById(R.id.checkCocidoNuevoPedido)).isChecked()));
                //Log.i("myapp", "Datosssss id Cat: "+id_Cat+ " id_Cli: " +id_Cli+ " id_Esp: "+especialidadSeleccionada.getId()+ "  pvp: "+especialidadSeleccionada.getPvp()+ " Ex: "+especialidadSeleccionada.getExistencias()+ "  Kg: "+kg+ " cocido: "+cocido);

                //2
                AnadirDetallePedido anadirDetallePedido = new AnadirDetallePedido();
                anadirDetallePedido.execute();

                //3
                //Update del pedido con el cliente seleccionado antes de mostrar los artículos, sino la consulta no es correcta
                UpdateClientePedido updateClientePedido = new UpdateClientePedido();
                updateClientePedido.execute();

                //4
                //Cargar la lista de artículos añadidos. Ojo que para pedido y Detallepedido utilizo el mismo array listaPedidos pq sólo tengo una clase Pedido
                LPedidoAnadido lPedidoAnadido = new LPedidoAnadido();
                lPedidoAnadido.execute();

                //5
                //Actualizar existencias
                UpdateExistencias updateExistencias = new UpdateExistencias();
                updateExistencias.execute();

                //6
                ((Spinner)findViewById(R.id.spinnerClienteNuevoPedido)).setEnabled(false);
            }
        }
    }

    public void comprobarPedidoVacio (final int id_P){
        String articulos="";
        ObtenerNumArticulos obtenerNumArticulos = new ObtenerNumArticulos(id_P);
        try{
            articulos = obtenerNumArticulos.execute().get();
        }catch (InterruptedException e) {
        e.printStackTrace();
        } catch (ExecutionException e) {
        e.printStackTrace();
        }

        //SI se genera un pedido al que no se le añaden artículos mostramos un cuadro de confirmación para borrarlo o mantenerlo.
        //Mejoramos eso, borramos siempre un pedido sin artículos. Comentamos el código para generar el cuadro de confirmación
        if ((Integer.valueOf(articulos)==0)) {
            /*
            //result =true;
            //Mostrar el cuadro de diálogo de pedido Vacio
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            //new AlertDialog.Builder(context)
            alertDialogBuilder
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Pedido sin artículos")
                .setMessage("Cojoncio has creado un pedido sin artículos añadidos. ¿Quieres borrarlo?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Async Task para eliminar el pedido vacío
                        DeletePedido deletePedido = new DeletePedido(id_P);
                        deletePedido.execute();
                        finish();
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Async Task para eliminar el pedido vacío
                        dialog.cancel();
                        finish();
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
                        //nuevopedido.this.finish();
                    }
                })
                .show();
            */
            DeletePedido deletePedido = new DeletePedido(id_P);
            deletePedido.execute();

        }/*else{
            finish();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
        }*/
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
    }

    public class ObtenerNumArticulos extends AsyncTask<Context, Integer, String>{
        int id_P;
        public ObtenerNumArticulos(int id_P)
        {
            this.id_P = id_P;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream articulos = obtenerContenido(urlApi+"peticion=pedidoVacio&id_P="+id_P);
                return getStringFromInputStream(articulos);
            } catch (Exception e) {
                Toast.makeText(nuevopedido.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String id_P) {
            super.onPostExecute(id_P);
        }
    }

    private class DeletePedido extends AsyncTask<Context, Integer, String> {
        int id_P;
        public DeletePedido (int id_P){
            this.id_P = id_P;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=deletePedido&id_E=1&id_P="+id_P);
            try{
                httpClient.execute(httpPost);
            }catch(Exception e){
                //Toast.makeText(PedidosAdapter, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Ocultar el loading
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        comprobarPedidoVacio(id_P);
        //overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
    }


    public void back (View v){
        comprobarPedidoVacio(id_P);
        //finish();
        //overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
    }


    public void irNuevoPedido(View v) throws ExecutionException, InterruptedException{
        int id_Ped=0;

        InsertarPedido insertarPedido = new InsertarPedido();
        insertarPedido.execute();

        ObtenerIdPedido obtenerIdPedido = new ObtenerIdPedido();
        id_Ped = Integer.valueOf(obtenerIdPedido.execute().get());

        //Tengo que extraer el id del pedido que acabo de insertar y enviárselo con putExtra a nuevopedido
        Intent i = new Intent(this, nuevopedido.class);
        i.putExtra("id_P", id_Ped);
        startActivity(i);
    }


    public void irListarPedidos(View v){
        comprobarPedidoVacio(id_P);
        Intent i = new Intent(this, ListarPedidos.class);
        startActivity(i);
    }


    public void irExistencias(View v){
        comprobarPedidoVacio(id_P);
        Intent i = new Intent(this, Existencias.class);
        startActivity(i);
    }


}
