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
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import static es.frios.pedidos.HttpManager.obtenerContenido;
import static es.frios.pedidos.HttpManager.getStringFromInputStream;


public class SelectorE extends Activity {
    //Vbles globales
    String[] spinnerE;

    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    private ArrayList<Empresa> listaEmpresas = new ArrayList<>();
    private int idE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selector_e);

        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tarea asincrona para traer todas las E. En la v2 se traerán sólo aquellas a las que el usuario pertenezca
        ListarEmpresas listarEmpresas = new ListarEmpresas();
        listarEmpresas.execute();
        //------------------------------------------------------------------------------------------------------------------------------------//


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void actualizar (View v){
        //------------------------------------------------------------------------------------------------------------------------------------//
        //Tarea asincrona para traer todas las E. En la v2 se traerán sólo aquellas a las que el usuario pertenezca
        ListarEmpresas listarEmpresas = new ListarEmpresas();
        listarEmpresas.execute();
        //------------------------------------------------------------------------------------------------------------------------------------//
    }

    public void actualizarEmpresa (View v){
        //Método para actualizar la Empresa tras pulsar el TIC de la parte superior derecha
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


    public void crearEmpresa (View v){
        Intent i = new Intent(this, CrearEmpresa.class);
        startActivity(i);
    }

    public void anadirmeEmpresa (View v){
        Intent i = new Intent(this, Anadirme.class);
        startActivity(i);
    }



    private class ListarEmpresas extends AsyncTask<Context, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar el loading
            ((ProgressBar) findViewById(R.id.progressBarSelectorE)).setVisibility(View.VISIBLE);
            Toast.makeText(SelectorE.this,"Obteniendo empresas...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream empresas = obtenerContenido(urlApi+"peticion=obtenerE");

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                return getStringFromInputStream(empresas);
            } catch (Exception e) {
                Toast.makeText(SelectorE.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("myapp", s);
                JSONArray json = new JSONArray(s);
                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    int id = json_data.getInt("id");
                    String nombreE = (String)  json_data.get("nombre");
                    String codigo = json_data.getString("codigo");
                    String gerente = json_data.getString("gerente");
                    String fecha_creacion = json_data.getString("fecha_creacion");
                    //String logo = json_data.getString("logo");
                    String LE = json_data.getString("LE");
                    String notificaciones = json_data.getString("notificaciones");

                    //Crear y almacenar el objeto
                    Empresa emp = new Empresa(id, nombreE, codigo, gerente, fecha_creacion, Boolean.valueOf(LE), Boolean.valueOf(notificaciones));
                    listaEmpresas.add(emp);
                }
                //Al salir del bucle ya tenemos el array de objetos modelo. Montar la lista

            }catch(JSONException e){
                Toast.makeText(SelectorE.this,"Datos incorrectos",Toast.LENGTH_SHORT).show();
            }

            //Ocultar el loading
            ((ProgressBar)findViewById(R.id.progressBarSelectorE)).setVisibility(View.GONE);
            //finish();

            //------------------------------------------------------------------------------------------------------------------------------------//
            //Cargamos el spinner
            //spinnerE = getResources().getStringArray(R.array.spinners);
            Spinner spinnerEmpresas = (Spinner)findViewById(R.id.spinnerSelectorE);
            SpinnerEmpresaAdapter adapter = new SpinnerEmpresaAdapter(SelectorE.this, R.layout.row_spinneremp, listaEmpresas);
            //Apariencia DropDown
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEmpresas.setAdapter(adapter);
            //Con el listener tenemos que coger el id de la E seleccionada
            spinnerEmpresas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int i = parent.getSelectedItemPosition();
                    //id Empresa
                    //idE= (int) (parent.getItemAtPosition(position)).get
                    //Toast.makeText(getBaseContext(),"Seleccionada la opción: " + spinnerE[i], Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //---------------------------------------------------------------------------------------------------//


        }
    }

}
