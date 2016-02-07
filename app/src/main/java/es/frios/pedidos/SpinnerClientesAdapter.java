package es.frios.pedidos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class SpinnerClientesAdapter extends ArrayAdapter<Cliente> {

    Context context;
    int resource; //representa al layout fila.
    List<Cliente> objects;


    public SpinnerClientesAdapter(Context context, int resource, List<Cliente> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    //FUNCIÓN QUE CREA LAS CELDAS (las filas)
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
            holder.txtCli = (TextView) row.findViewById(R.id.txtClienteRowSpinnerCli);
            row.setTag(holder);
        }else{
            //la reutilizaos
            holder=(Holder) row.getTag();
        }

        //PONEMOS LOS TEXTOS EN LOS ELEMENTOS DEL LAYOUT
        holder.txtCli.setText((objects.get(position).getNombre())+" "+(objects.get(position).getApellidos()));

        return row;
    }


    //UNA CLASE QUE REPRESENTA A NUESTRO LAYOUT ROW
    static class Holder{
        //CREAMOS TANTOS ATRIBUTOS COMO OBJETOS TENGA LA VISTA ROW
        TextView txtCli;




    }

}
