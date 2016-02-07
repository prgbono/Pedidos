package es.frios.pedidos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;


public class NuevoPedidoAdapter extends ArrayAdapter<Pedido> {

    Context context;
    int resource;
    List<Pedido> data=null;

    public NuevoPedidoAdapter(Context context, int resource, List<Pedido> data) {
        super(context, resource, data);
        this.context=context;
        this.resource=resource;
        this.data=data;
    }


    public View getView(int position,View convertView,ViewGroup parent){
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
            holder.textCat = (TextView)row.findViewById(R.id.txtCatRowNuevoPedido);
            holder.textEsp = (TextView)row.findViewById(R.id.txtEspRowNuevoPedido);
            holder.textPvp = (TextView) row.findViewById(R.id.txtPrecioRowNuevoPedido);
            holder.textKg = (TextView) row.findViewById(R.id.txtKgRowNuevoPedido);



            row.setTag(holder);


        }else{
            //la reutilizaos
            holder=(Holder) row.getTag();
        }


        //PONEMOS LOS TEXTOS EN LOS ELEMENTOS DEL LAYOUT
        holder.textCat.setText(data.get(position).getCategoria());
        holder.textEsp.setText(data.get(position).getEspecialidad());
        holder.textPvp.setText(String.valueOf(data.get(position).getPvp()));
        holder.textKg.setText(String.valueOf(data.get(position).getKg()));



        return row;
    }



    //UNA CLASE QUE REPRESENTA A NUESTRO LAYOUT ROW
    static class Holder{
        //CREAMOS TANTOS ATRIBUTOS COMO OBJETOS TENGA LA VISTA ROW
        TextView textCat;
        TextView textEsp;
        TextView textPvp;
        TextView textKg;


    }

}

