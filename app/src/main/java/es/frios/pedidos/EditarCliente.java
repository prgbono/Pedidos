package es.frios.pedidos;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import es.frios.pedidos.Manager.InsertarPedido;
import es.frios.pedidos.Manager.ObtenerIdPedido;



public class EditarCliente extends Activity {

    //Vbles globales de la clase
    private Cliente cliente;
    int id_P=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_editar_cliente);

        //Obtenemos los datos del cliente sobre el que ha pulsado
        //---------------------------------------------------------------------------------------------------//
        Intent i = getIntent();
        cliente = (Cliente)i.getSerializableExtra("cliente");
        ((EditText)findViewById(R.id.editNombreEditarCliente)).setText(cliente.getNombre());
        ((EditText)findViewById(R.id.editApellEditarCliente)).setText(cliente.getApellidos());
        ((EditText)findViewById(R.id.editTlfEditarCliente)).setText(cliente.getTlf());
        ((EditText)findViewById(R.id.editEnvioEditarCliente)).setText(cliente.getEnvio());
        ((EditText)findViewById(R.id.editCiudadEditarCliente)).setText(cliente.getCiudad());
        ((EditText)findViewById(R.id.editCorreoEditarCliente)).setText(cliente.getCorreo());
        ((CheckBox)findViewById(R.id.checkBBarEditarCliente)).setChecked(cliente.isBar());
        //---------------------------------------------------------------------------------------------------//
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

    public void irNuevoPedido(View v) throws ExecutionException, InterruptedException{
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

    public void eliminarCliente(View v){
        //Método para eliminar cliente a la empresa en curso
    }

    public void actualizarCliente(View v){

    }




}
