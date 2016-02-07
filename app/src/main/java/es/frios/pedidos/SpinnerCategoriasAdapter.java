package es.frios.pedidos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class SpinnerCategoriasAdapter extends ArrayAdapter<Categoria> {

    Context context;
    int resource; //representa al layout fila.
    List<Categoria> objects;


    public SpinnerCategoriasAdapter(Context context, int resource, List<Categoria> objects) {
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
            holder.txtCat = (TextView) row.findViewById(R.id.txtCatRowSpinnerCat);
            //holder.spinner = (Spinner) row.findViewById(R.id.spinnerSelectorE);
            row.setTag(holder);
        }else{
            //la reutilizaos
            holder=(Holder) row.getTag();
        }

        //PONEMOS LOS TEXTOS EN LOS ELEMENTOS DEL LAYOUT
        holder.txtCat.setText(objects.get(position).getNombre());

        //___________________________________________________________________________________
        //holder.spinner.setTsetText(objects.get(position).getNombre());
        //___________________________________________________________________________________



        return row;
    }


    //UNA CLASE QUE REPRESENTA A NUESTRO LAYOUT ROW
    static class Holder{
        //CREAMOS TANTOS ATRIBUTOS COMO OBJETOS TENGA LA VISTA ROW
        TextView txtCat;
        //Spinner spinner;



    }

}
