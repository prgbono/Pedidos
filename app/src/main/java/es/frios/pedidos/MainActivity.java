package es.frios.pedidos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import static es.frios.pedidos.HttpManager.obtenerContenido;
import es.frios.pedidos.Manager.InsertarPedido;
import es.frios.pedidos.Manager.ObtenerIdPedido;


public class MainActivity extends Activity {

    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    int id_P=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_slide_out_bottom);
    }


    public void irSelectorE(View v){
        Intent i = new Intent(this, SelectorE.class);
        startActivity(i);
    }


    public void irUpdateE(View v){
        Intent i = new Intent(this, UpdateEmpresa.class);
        startActivity(i);
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


    /*private class ObtenerIdPedido extends AsyncTask<Context, Integer, String>{
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
                Toast.makeText(MainActivity.this,"Error de conexión", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class InsertarPedido extends AsyncTask<Context, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            //Calculamos la fecha actual para incluirla como fecha de entrega del pedido. Esto se hace por defecto, se puede editar
            //El parámetro calcularFecha() inserta la fecha actual. Los campos id_Cliente e importe deberán ser actualizados posteriormente
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=insertarPedido&id_E=1&fecha_entrega="+calcularFecha()+"&importe=0"); //para la v2 calcular id_E. Id_Cli e importe hay que actualizarlos posteriormente
            try{
                //Log.i("myapp","id_Cli: "+id_Cli);
                httpClient.execute(httpPost);

                //La comprobación de que se ha insertado será que desaparecerá la lista de los artículos añadidos

            }catch(Exception e){
                Toast.makeText(MainActivity.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
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
    }*/


    public void irListarPedidos(View v){
        Intent i = new Intent(this, ListarPedidos.class);
        startActivity(i);
    }


    public void irExistencias(View v){
        Intent i = new Intent(this, Existencias.class);
        startActivity(i);
    }









}
