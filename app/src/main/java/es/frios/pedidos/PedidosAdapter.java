package es.frios.pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.getIntent;
import static android.view.View.OnClickListener;

public class PedidosAdapter extends ArrayAdapter<Pedido>{
    
    Context context;
    int resource;
    List<Pedido> data=null;

    //private String urlApi = "http://rios.esy.es/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://localhost:8888/pedidos/ApiPedidos.php?";
    //private String urlApi = "http://192.168.1.104:8888/pedidos/ApiPedidos.php?";
    private String urlApi = "http://10.0.3.2:8888/pedidos/ApiPedidos.php?";

    public PedidosAdapter(Context context, int resource, List<Pedido> data) {
        super(context, resource, data);
        this.context=context;
        this.resource=resource;
        this.data=data;

    }


    public View getView(final int position, final View convertView,ViewGroup parent){
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
            holder.textCliente = (TextView)row.findViewById(R.id.txtClienteRawListaPedidos);
            holder.textFechaRecogida = (TextView) row.findViewById(R.id.txtFechaRRawListaPedidos);
            holder.textImporte = (TextView) row.findViewById(R.id.txtImporteRawListaPedidos);
            holder.checkPagado = (CheckBox) row.findViewById(R.id.chckBPagadoRawListaPedidos);
            holder.btnEliminar = (ImageView)row.findViewById(R.id.btnEliminarRowListaPedidos);

            row.setTag(holder);

        }else{
            //la reutilizaos
            holder=(Holder) row.getTag();
        }


        //PONEMOS LOS TEXTOS EN LOS ELEMENTOS DEL LAYOUT
        holder.textCliente.setText(data.get(position).getCliente());
        holder.textFechaRecogida.setText(data.get(position).getFecha_entrega());
        holder.textImporte.setText(String.valueOf(data.get(position).getImporte())+"€");

        //Falta pasar el checkbox
        holder.checkPagado.setChecked(data.get(position).isPagado());
        //holder.checkPagado.setOnClickListener(R.id.chckBPagadoRawListaPedidos);

        holder.btnEliminar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("PedidosAdapter dd: ", String.valueOf(getItem(position).getId()));
                //Log.i("PedidosAdapter", "ENtRA EN BUTTON ELIMINAR");

                //Mostrar un mensaje de confirmación del borrado
                new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Eliminar pedido")
                    .setMessage("¿Eliminar el pedido y sus artículos?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Async Task para eliminar el pedido al completo
                            DeletePedido deletePedido = new DeletePedido(getItem(position).getId());
                            deletePedido.execute();
                            ((Activity) context).finish();

                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
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
        TextView textCliente;
        TextView textFechaRecogida;
        TextView textImporte;
        CheckBox checkPagado;
        ImageView btnEliminar;



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
            //Log.i("PedidosAdapter AT: ", String.valueOf(id_P)); //En id_P está el id del pedido a eliminar
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlApi+"peticion=deletePedido&id_E=1&id_P="+id_P);
            HttpPost httpPost2 = new HttpPost(urlApi+"peticion=deleteDetallesPedido&id_P="+id_P);
            try{
                httpClient.execute(httpPost);
                httpClient.execute(httpPost2);
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

}
