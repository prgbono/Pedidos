package es.frios.pedidos;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.app.DatePickerDialog;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import static es.frios.pedidos.HttpManager.obtenerContenido;
import static es.frios.pedidos.R.id.listDetallePedido;
import es.frios.pedidos.Manager.InsertarPedido;
import es.frios.pedidos.Manager.ObtenerIdPedido;



public class DetallePedido extends Activity {
    //Vbles globales de la clase

    //Vbles globales
    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    int id_P=0;

    //Arrays de objetos
    ArrayList<Cliente> listaClientes = new ArrayList<>();
    ArrayList<Pedido> listaPedidos = new ArrayList<>();

    private Pedido pedido;
    //int id_Cli;  //id de cliente
    //String[] spinnerCli;

    private TextView fecha_entrega;
    private Button cambiar_fecha;
    static final int DATE_DIALOG_ID=0;
    private int year, nummes, dia;
    private String mes;
    private String[] catYesp= new String[2]; //Array de dos posiciones que guardará la categoría y la especialidad de cada detalle del pedido (está en un for)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detalle_pedido);

        //Obtenemos los datos del pedido sobre el que ha pulsado
        Intent i = getIntent();
        pedido = (Pedido)i.getSerializableExtra("pedido");
        //id_Cli = (int) i.getSerializableExtra("position");
        //Log.i("DetallePedido","id_Cli (position): "+id_Cli);

        //AsyncTask para cargar los datos del pedido seleccionado en la lista inferior de la pantalla
        CargarDetalles cargarDetalles = new CargarDetalles();
        cargarDetalles.execute();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    //--------------------------------------------------------------------------------------------------------------------//
    //FUNCIONES PARA EL botón DataPicker
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, year, nummes, dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener(){
        public void onDateSet(DatePicker view, int year_aux, int monthOfYear, int dayOfMonth){
         year = year_aux;
         nummes = monthOfYear;
         mes = mes(nummes);
         dia = dayOfMonth;
         fecha_entrega.setText(new StringBuilder().append(dia).append("-").append(mes).append("-").append(year));
        }
    };
    //--------------------------------------------------------------------------------------------------------------------//


    public String mes(int nummes){
        switch (nummes){
            case 0: return "ENE";
            case 1: return "FEB";
            case 2: return "MAR";
            case 3: return "ABR";
            case 4: return "MAY";
            case 5: return "JUN";
            case 6: return "JUL";
            case 7: return "AGO";
            case 8: return "SEP";
            case 9: return "OCT";
            case 10: return "NOV";
            case 11: return "DIC";
            default: return null;
        }
    }

    private class CargarDetalles extends AsyncTask<Context, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar el loading. HAY que poner un ProgressBar en la vista
            ((ProgressBar) findViewById(R.id.progressBarDetallePedido)).setVisibility(View.VISIBLE);
            Toast.makeText(DetallePedido.this, "Cargando datos de este pedido...", Toast.LENGTH_SHORT).show();


            //Nombre Cliente
            ((TextView)findViewById(R.id.txtClienteDetallePedido)).setText(pedido.getCliente()); //En la v2 cargar aquí todos los clientes para que se pueda modificar tb el cliente

            //Fecha_entrega (DatePicker)
            fecha_entrega = (TextView)findViewById(R.id.txtFechaRecogidaDetallePedido);
            cambiar_fecha = (Button)findViewById(R.id.btnCambiarFechaDetallePedido);
            //Establecemos un listener para el botón cambiar de fecha
            cambiar_fecha.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    dia = cal.get(Calendar.DAY_OF_MONTH);
                    nummes = cal.get(Calendar.MONTH);
                    mes = mes(nummes);
                    year = cal.get(Calendar.YEAR);
                    showDialog(DATE_DIALOG_ID);   //DEPRECATED!!!!
                }
            });

            if (!pedido.getFecha_entrega().isEmpty()){
                fecha_entrega.setText(pedido.getFecha_entrega().toString());
            }else{
                //traer la fecha actual o establecer la que el usuario ponga en el DatePicker del botón cambiar
                Calendar cal = Calendar.getInstance();
                dia = cal.get(Calendar.DAY_OF_MONTH);
                nummes = cal.get(Calendar.MONTH);
                mes = mes(nummes);
                year = cal.get(Calendar.YEAR);
                fecha_entrega.setText(new StringBuilder().append(dia).append("-").append(mes).append("-").append(year));
            }

            //Importe
            ((EditText)findViewById(R.id.editImporteTotalDetallePedido)).setText(String.valueOf(pedido.getImporte())+"€");

            //Booleanos de envío y pagado
            ((CheckBox)findViewById(R.id.checkPagadoDetallePedido)).setChecked(pedido.isPagado());
            ((CheckBox)findViewById(R.id.checkEnvioDetallePedido)).setChecked(pedido.isEnvioBool());

            //Dirección de envío --> V2
            // Falta añadir a la clase Pedido el atributo String de la dirección de envío ((EditText)findViewById(R.id.editEnvioDetallePedido)).setText(pedido.get);

            //Comentarios sobre el pedido
            ((EditText)findViewById(R.id.editComentariosDetallePedido)).setText(pedido.getComentarios());
            //---------------------------------------------------------------------------------------------------//


        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream detallePedido = obtenerContenido(urlApi+"peticion=listarAnadidosConIdPedido&id_P="+pedido.getId());

                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                return getStringFromInputStream(detallePedido);
            } catch (Exception e) {
                Toast.makeText(DetallePedido.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Pasar el String que viene de doItBackground a JSON
            try{
                Log.i("myapp", s);
                int id_Pedido,id_detalle, id_Cat, id_Esp;
                double pvp, kg;
                JSONArray json = new JSONArray(s);

                //limpiamos el array
                listaPedidos.clear();

                for (int i=0;i<json.length();i++){
                    JSONObject json_data = json.getJSONObject(i);
                    id_Pedido = json_data.getInt("id_Pedido");
                    id_detalle = json_data.getInt("id");
                    id_Cat = json_data.getInt("id_Cat");
                    id_Esp = json_data.getInt("id_Esp");

                    //aquí hay que obtener el nombre de categoría y especialidad. Para ello usamos la AsyncTask obtenerCatyEsp que devolverá los nombres en un array de dos posiciones
                    ObtenerCatyEsp obtenerCatyEsp = new ObtenerCatyEsp();
                    catYesp=obtenerCatyEsp.execute(id_Cat, id_Esp).get();
                    Log.i("DetallePedido", "CAt y Esp: "+catYesp[0]+"..."+catYesp[1]);

                    pvp  = Double.valueOf(BigDecimal.valueOf(json_data.getDouble("pvp")).toPlainString());
                    kg  = Double.valueOf(BigDecimal.valueOf(json_data.getDouble("kg")).toPlainString());
                    //double pvpAcordado  = Double.valueOf(BigDecimal.valueOf(json_data.getDouble("pvp_acordado")).toPlainString());

                    Log.i("myapp","Cat: "+id_Cat+"  esp: "+id_Esp+"  pvp: "+pvp+"  kg: "+kg);

                    //Crear y almacenar el objeto
                    Pedido detallePedido = new Pedido(id_detalle, id_Pedido, catYesp[0], catYesp[1], pvp, kg);
                    listaPedidos.add(detallePedido);
                }
                //Al salir del bucle ya tenemos el array de los artículos del pedido. Antes de montar la lista debemos obtener lon nombres de la categoría y especialidad de cada artículo.
                DetallePedidoAdapter adapter = new DetallePedidoAdapter(DetallePedido.this, R.layout.row_detallepedido, listaPedidos);
                ((ListView)findViewById(R.id.listDetallePedido)).setAdapter(adapter);

            }catch(JSONException e) {
                Toast.makeText(DetallePedido.this, "Datos incorrectos4", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //Ocultar el loading
            ((ProgressBar)findViewById(R.id.progressBarDetallePedido)).setVisibility(View.GONE);



        }
    }

    private class ObtenerCatyEsp extends AsyncTask<Integer, Integer, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Integer... params) {
            int id_Cat=params[0];
            int id_Esp=params[1];
            String[] result= new String[2];
            try {
                //Recibir el Inputstream con el contenido mediante el método correspondiente de la clase HttpManager
                InputStream nombreCat = obtenerContenido(urlApi+"peticion=obtenerNombreCat&id_E=1&id_Cat="+id_Cat);
                //REtornar el contenido en forma de String mediante el método getStrinfFromInputStream de la clase HttpManager
                result[0]=getStringFromInputStream(nombreCat);
                InputStream nombreEsp = obtenerContenido(urlApi+"peticion=obtenerNombreEsp&id_E=1&id_Esp="+id_Esp);
                result[1]=getStringFromInputStream(nombreEsp);
                return result;
            } catch (Exception e) {
                Toast.makeText(DetallePedido.this,"Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
        }

    }

    private class UpdatePedidos extends AsyncTask<Context, Integer, String> {
        String fecha_entrega, comentarios;
        int envio_bool, pagado;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar el loading.
            ((ProgressBar) findViewById(R.id.progressBarActualizandoDetallePedido)).setVisibility(View.VISIBLE);
            Toast.makeText(DetallePedido.this, "Actualizando pedido...", Toast.LENGTH_SHORT).show();

            //1.Obtenemos los datos introducidos (fecha, booleanos de envío y pago y comentarios)
            fecha_entrega = ((TextView)findViewById(R.id.txtFechaRecogidaDetallePedido)).getText().toString();
            CheckBox checkBox = (CheckBox) findViewById(R.id.checkEnvioDetallePedido);
            if (checkBox.isChecked()) {
                envio_bool = 1;
            } else {
                envio_bool = 0;
            }
            checkBox = (CheckBox) findViewById(R.id.checkPagadoDetallePedido);
            if (checkBox.isChecked()) {
                pagado = 1;
            } else {
                pagado = 0;
            }
            comentarios = ((EditText)findViewById(R.id.editComentariosDetallePedido)).getText().toString().trim();
        }

        @Override
        protected String doInBackground(Context... params) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=updatePedido&id_E=1&id_P="+pedido.getId());

            //Datos a enviar a PHP
            ArrayList<NameValuePair> updPedido = new ArrayList<>();
            updPedido.add(new BasicNameValuePair("fecha_entrega", fecha_entrega));
            updPedido.add(new BasicNameValuePair("bool_envio", String.valueOf(envio_bool)));
            updPedido.add(new BasicNameValuePair("pagado", String.valueOf(pagado)));
            updPedido.add(new BasicNameValuePair("comentarios", comentarios));

            //Insertar el paquete 'updPedido' en la petición y enviarlo
            try{
                httpPost.setEntity(new UrlEncodedFormEntity(updPedido));
                httpClient.execute(httpPost);
            }catch(Exception e){
                Toast.makeText(DetallePedido.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Ocultar el loading
            ((ProgressBar)findViewById(R.id.progressBarActualizandoDetallePedido)).setVisibility(View.GONE);
            Toast.makeText(DetallePedido.this, "Pedido actualizado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void actualizarPedido (View v){
        //Método para actualizar el pedido tras pulsar el (botón inferior o el TIC de la parte superior derecha)
        //2. Hacemos el update del pedido en la BBDD ayudándonos de una AsyncTask
        UpdatePedidos updatePedidos = new UpdatePedidos();
        updatePedidos.execute();
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


    public void irNuevoPedido(View v) throws ExecutionException, InterruptedException {
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



}
