package es.frios.pedidos;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ClientesAdapter extends ArrayAdapter<Cliente> {

    Context context;
    int resource;
    List<Cliente> data=null;

    public ClientesAdapter(Context context, int resource, List<Cliente> data) {
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

            //INFLATE ES EL ENCARGADO DE UNIR layout -> información a mostrar
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row=inflater.inflate(resource, parent,false);

            holder = new Holder();

            //UNIMOS==> vista con controlador
            holder.textNombre = (TextView)row.findViewById(R.id.txtNombreRowCliente);
            //holder.textApellidos = (TextView) row.findViewById(R.id.txtApellRowCliente);
            holder.textTlf = (TextView) row.findViewById(R.id.txtTlfRowCliente);



            row.setTag(holder);


        }else{
            //la reutilizaos
            holder=(Holder) row.getTag();
        }


        //PONEMOS LOS TEXTOS EN LOS ELEMENTOS DEL LAYOUT
        holder.textNombre.setText(data.get(position).getNombre()+" "+data.get(position).getApellidos());
        //holder.textApellidos.setText(data.get(position).getApellidos());
        holder.textTlf.setText(data.get(position).getTlf());



        return row;
    }



    //UNA CLASE QUE REPRESENTA A NUESTRO LAYOUT ROW
    static class Holder{
        //CREAMOS TANTOS ATRIBUTOS COMO OBJETOS TENGA LA VISTA ROW
        TextView textNombre;
        //TextView textApellidos;
        TextView textTlf;


    }

}
