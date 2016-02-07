package es.frios.pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static es.frios.pedidos.HttpManager.getStringFromInputStream;
import static es.frios.pedidos.HttpManager.obtenerContenido;

public class DetallePedidoAdapter extends ArrayAdapter<Pedido> {

    Context context;
    int resource;
    List<Pedido> data=null;

    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    public DetallePedidoAdapter(Context context, int resource, List<Pedido> data) {
        super(context, resource, data);
        this.context=context;
        this.resource=resource;
        this.data=data;
    }


    public View getView(final int position,View convertView,ViewGroup parent){
        //ATRIBUTOS
        View row = convertView; //view row
        Holder holder = null;//contenedor

        //1- REUTILIZACIÓN DE CELDAS
        if(row == null){
            //tendremos que crear filas/celdas

            //?????
            //INFLATE ES EL ENCARGADO DE UNIR layout -> información a mostrar
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row=inflater.inflate(resource, parent,false);

            holder = new Holder();

            //UNIMOS==> vista con controlador
            holder.textCat = (TextView)row.findViewById(R.id.txtCatRowDetallePedido);
            holder.textEsp = (TextView)row.findViewById(R.id.txtEspRowDetallePedido);
            holder.textPvp = (TextView) row.findViewById(R.id.txtPvpRowDetallePedido);
            holder.textKg = (TextView) row.findViewById(R.id.txtKgRowDetallePedido);
            holder.btnEliminar = (ImageView)row.findViewById(R.id.ImgDeleteRowDetallePedido);


            row.setTag(holder);


        }else{
            //la reutilizaos
            holder=(Holder) row.getTag();
        }


        //PONEMOS LOS TEXTOS EN LOS ELEMENTOS DEL LAYOUT
        holder.textCat.setText(data.get(position).getCat());
        holder.textEsp.setText(data.get(position).getEspecialidad());
        holder.textPvp.setText(String.valueOf(data.get(position).getPvp()));
        holder.textKg.setText(String.valueOf(data.get(position).getKg()));



        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("PedidosAdapter dd: ", String.valueOf(getItem(position).getId()));
                //Log.i("DetallePedidoAdapter", "ENtRA EN BUTTON ELIMINAR");

                //Distinguimos si el artículo a eliminar es el último que queda en el pedido. En tal caso borraríamos también el pedido
                String articulos="";
                ObtenerNumArticulos obtenerNumArticulos = new ObtenerNumArticulos(getItem(position).getId());
                Log.i("DetallePedidoAdapter","id_P: "+getItem(position).getId());
                try{
                    articulos = obtenerNumArticulos.execute().get();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if ((Integer.valueOf(articulos)==1)) {
                    //Se trata del último artículo de este pedido
                    //Mostrar el cuadro de diálogo
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    //new AlertDialog.Builder(context)
                    alertDialogBuilder
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("El pedido se queda sin artículos")
                        .setMessage("Se eliminará el artículo y el pedido. ¿Continuar?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Async Task para eliminar el pedido vacío
                                DeleteDetallePedido DeleteDetallePedido = new DeleteDetallePedido(getItem(position).getId_detalle());
                                DeleteDetallePedido.execute();
                                DeletePedido deletePedido = new DeletePedido(getItem(position).getId());
                                deletePedido.execute();
                                ((Activity) context).finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Async Task para eliminar el pedido vacío
                                dialog.cancel();
                                //nuevopedido.this.finish();
                            }
                        })
                        .show();

                }else{
                    //Queda más de un artículo en el pedido
                    //Mostrar un mensaje de confirmación del borrado
                    new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eliminar artículo")
                        .setMessage("¿Eliminar este artículo del pedido?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Async Task para eliminar el pedido al completo
                                DeleteDetallePedido DeleteDetallePedido = new DeleteDetallePedido(getItem(position).getId_detalle());
                                DeleteDetallePedido.execute();
                                //Actualizar existencias del pedido si éste sigue existiendo
                                CalcularImporte calcularImporte = new CalcularImporte(getItem(position).getId());
                                calcularImporte.execute();
                                ((Activity) context).finish();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();




                }

                //---------------------------------------------------------------------------------------
                //Hay que recargar la lista de pedidos para que no muestre el objeto recién eliminado. Aquí????
                //---------------------------------------------------------------------------------------
            }



        });


        return row;
    }



    //UNA CLASE QUE REPRESENTA A NUESTRO LAYOUT ROW
    static class Holder{
        //CREAMOS TANTOS ATRIBUTOS COMO OBJETOS TENGA LA VISTA ROW
        TextView textCat;
        TextView textEsp;
        TextView textPvp;
        TextView textKg;
        ImageView btnEliminar;
    }


    private class DeleteDetallePedido extends AsyncTask<Context, Integer, String> {
        int id_DP;

        public DeleteDetallePedido (int id_DP){
            this.id_DP = id_DP;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            Log.i("DetallePedidoAdapter AT: ", String.valueOf(id_DP)); //En id_DP está el id del detalle a eliminar
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=deleteDetallePedido&id_DP="+id_DP);
            try{
                httpClient.execute(httpPost);
            }catch(Exception e){
                Log.i("DetallePedidoAdapter AT: ", "Entra en el catch");
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
                //Toast.makeText(nuevopedido.this, "Error de conexión. Inténtalo más tarde", Toast.LENGTH_SHORT).show();
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

        public DeletePedido(int id_P) {
            this.id_P = id_P;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi + "peticion=deletePedido&id_E=1&id_P=" + id_P);
            try {
                httpClient.execute(httpPost);
            } catch (Exception e) {
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
                Log.i("DetallePedidoAdapter", "Catch del updatePedido");
                //Toast.makeText(ListarPedidos.this, "Se ha producido un error. Por favor, revise su conexión o inténtelo más tarde", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {super.onPostExecute(s);}
    }

}
