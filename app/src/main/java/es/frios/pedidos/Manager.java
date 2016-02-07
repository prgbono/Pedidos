package es.frios.pedidos;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Calendar;

import java.io.InputStream;
import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import static es.frios.pedidos.HttpManager.obtenerContenido;

public class Manager {



    public static class InsertarPedido extends AsyncTask<Context, Integer, String> {
        //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
        //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
        //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
        private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

        static Activity thisActivity = null; //para el context del Toast

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
                Toast.makeText(thisActivity, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }


    public static class ObtenerIdPedido extends AsyncTask<Context, Integer, String>{
        //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
        //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
        //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
        private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

        static Activity thisActivity = null;

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
                Toast.makeText(thisActivity,"Error de conexión", Toast.LENGTH_SHORT).show();
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

    public static String calcularFecha(){
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int nummes = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        String mes = mes(nummes);
        return (dia+"-"+mes+"-"+year);

    }

    public static String mes(int nummes){
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

}
