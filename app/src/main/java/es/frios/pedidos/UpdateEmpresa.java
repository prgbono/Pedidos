package es.frios.pedidos;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.concurrent.ExecutionException;

import es.frios.pedidos.Manager.InsertarPedido;
import es.frios.pedidos.Manager.ObtenerIdPedido;


public class UpdateEmpresa extends Activity {

    int id_P=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_empresa);

    }


    public void actualizarEmpresa (View v){
        //Método para actualizar la Empresa tras pulsar el TIC de la parte superior derecha
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




}
