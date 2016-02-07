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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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



public class Existencias extends Activity {
    //Vbles globales

    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    int id_P=0;

    //Arrays con objetos modelo
    ArrayList<Categoria> listaCategorias = new ArrayList<>();
    ArrayList<Especialidad> listaEspecialidades = new ArrayList<>();

    int id_Cat=1; //id de categoría que obtenemos en una AsyncTask y usamos en otra. En la primera llamada valdrá 1 pero después se reclacula. Mejorar esto en la v2
    double existencias;
    Especialidad especialidadSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_existencias);

        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tareas asincronas para traer todas las categorías y especialidades existentes en la E para cargar los spinners.
        ListarCategorias listarCategorias = new ListarCategorias();
        listarCategorias.execute();
        //Esta AsyncTask necesita este par try-catch pq se le pasa un entero
        try {
            ListarEspecialidades listarEspecialidades = new ListarEspecialidades(id_Cat);
            listarEspecialidades.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------------------------------------------------//

    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    //Recarga las listas
    public void actualizar(View v){
        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tareas asincronas para traer todas las categorías y especialidades existentes en la E para cargar los spinners.
        ListarCategorias listarCategorias = new ListarCategorias();
        listarCategorias.execute();
        //Esta AsyncTask necesita este par try-catch pq se le pasa un entero
        try {
            ListarEspecialidades listarEspecialidades = new ListarEspecialidades(id_Cat);
            listarEspecialidades.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------------------------------------------------//
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
                Toast.makeText(Existencias.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                //Log.i("myapp", s);
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
                Toast.makeText(Existencias.this,"Datos incorrectos",Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            //((ProgressBar)findViewById(R.id.progressBarSelectorE)).setVisibility(View.GONE);
            //finish();

            //------------------------------------------------------------------------------------------------------------------------------------//
            //Cargamos el spinner
            //spinnerE = getResources().getStringArray(R.array.spinners);
            Spinner spinnerCat = (Spinner)findViewById(R.id.spinnerCatExistencias);
            SpinnerCategoriasAdapter adapter = new SpinnerCategoriasAdapter(Existencias.this, R.layout.row_spinnercat, listaCategorias);
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
                Toast.makeText(Existencias.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Existencias.this,"Datos incorrectos",Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            //((ProgressBar)findViewById(R.id.progressBarSelectorE)).setVisibility(View.GONE);
            //finish();

            //------------------------------------------------------------------------------------------------------------------------------------//
            //Cargamos el spinner
            Spinner spinnerEsp = (Spinner)findViewById(R.id.spinnerEspExistencias);
            SpinnerEspecialidadesAdapter adapter = new SpinnerEspecialidadesAdapter(Existencias.this, R.layout.row_spinneresp, listaEspecialidades);
            //Apariencia DropDown
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEsp.setAdapter(adapter);
            //adapter.notifyDataSetChanged();


            //Con el listener tenemos que coger el id de la E seleccionada
            spinnerEsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int i = parent.getSelectedItemPosition();
                    //Obtener especialidad seleccionada
                    especialidadSeleccionada = listaEspecialidades.get(position);
                    ((EditText) findViewById(R.id.editPvpExistencias)).setText(Double.toString(especialidadSeleccionada.getPvp()));
                    ((EditText) findViewById(R.id.editExistenciasExistencias)).setText(Double.toString(especialidadSeleccionada.getExistencias()));
                    //sLog.i("myapp", "Entra en onItemClickListener del spinner de especialidades. Esp seleccionada: "+especialidadSeleccionada.getEspecialidad());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //---------------------------------------------------------------------------------------------------//


        }
    }

    public void actualizarExistencias (View v){
        if ((((EditText)findViewById(R.id.editKgExistencias)).getText().toString()).isEmpty()){
            Toast.makeText(Existencias.this,"Necesario número de Kgs", Toast.LENGTH_SHORT).show();
        }else{
            //Llamamos a la AT que nos devuelve las existencias actuales
            UpdateExistencias updateExistencias = new UpdateExistencias();
            updateExistencias.execute();
        }
    }

    private class UpdateExistencias extends AsyncTask<Context, Integer, String>{
        //vbles
        int id_Categoria, id_Especialidad;
        double kg, existencias, pvp_coste, pvp;


        @Override
        protected void onPreExecute() {
            //Captar datos de empresa, categoría, especialidad, pvp, precio coste, kg y existencias introducidos por el usuario
            id_Categoria = ((Categoria)((Spinner)findViewById(R.id.spinnerCatExistencias)).getSelectedItem()).getId();  // tb se podría obtener como id_Categoria = especialidadSeleccionada.getId_Cat();
            id_Especialidad = ((Especialidad)((Spinner)findViewById(R.id.spinnerEspExistencias)).getSelectedItem()).getId();  // tb se podría obtener como id_Categoria = especialidadSeleccionada.getId();
            pvp = Double.valueOf(((EditText)findViewById(R.id.editPvpExistencias)).getText().toString());
            //pvp_coste = Double.valueOf(((EditText)findViewById(R.id.editPCosteExistencias)).getText().toString());
            kg = Double.valueOf(((EditText)findViewById(R.id.editKgExistencias)).getText().toString());
            //existencias = Double.valueOf(((EditText)findViewById(R.id.editExistenciasExistencias)).getText().toString());
            Log.i("Existencias","id_Categoria:"+id_Categoria+", id_Especialidad:"+id_Especialidad+", pvp:"+pvp+", pvp_Coste:"+pvp_coste+", kg:"+kg+", existencias:"+existencias);

            //V2
            //Comprobar datos (valores negativos, los kg ...) Comprobar tb que si quedan kg el pvp sea el mismo (v2)...
        }


        @Override
        protected String doInBackground(Context... params) {
            //Añadir los kilos introducidos a las existencias existentes de la especialidad seleccionada
            //TRAER LAS EXISTENCIAS ACTUALES Y SUMARLE LOS KG PUESTOS DEL USUARIO.
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=updateExistencias&id_Esp="+id_Especialidad+"&pvp="+pvp+"&existencias="+(especialidadSeleccionada.getExistencias()+kg));
            try{
                httpClient.execute(httpPost);
            }catch(Exception e){
                Toast.makeText(Existencias.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(Existencias.this, "Existencias actualizadas", Toast.LENGTH_SHORT).show();
            //finish();

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


    public void irListarPedidos(View v){
        Intent i = new Intent(this, ListarPedidos.class);
        startActivity(i);
    }





}
