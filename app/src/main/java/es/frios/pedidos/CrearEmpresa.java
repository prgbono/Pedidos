package es.frios.pedidos;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class CrearEmpresa extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crear_empresa);

    }


    public void crearEmpresa (View v){
        //Método para crear Empresa tras pulsar el TIC de la parte superior derecha
        //Este método comprueba los datos introducidos y en caso de ser correctos le pasa el objeto datosEmpresa con los datos introducidos a la activity 'CodigoEmpresa' donde se mostrará el código de empresa recién creada en caso de éxito o Error eoc

        String nombreEmpresa = ((EditText)findViewById(R.id.editNombreECrearEmpresa)).getText().toString().trim();
        String nombreGerente = ((EditText)findViewById(R.id.editNombreGerenteCrearEmpresa)).getText().toString().trim();
        String codigo = ((EditText)findViewById(R.id.editContrasenaCrearEmpresa)).getText().toString().trim();
        String confirmarCodigo = ((EditText)findViewById(R.id.editConfirmarContrasenaCrearEmpresa)).getText().toString().trim();

        //checkboxes
        boolean LE, notificaciones;
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkLECrearEmpresa);
        if (checkBox.isChecked()) {
            LE = true;
        } else {
            LE = false;
        }
        checkBox = (CheckBox) findViewById(R.id.checkNotificacionesCrearEmpresa);
        if (checkBox.isChecked()) {
            notificaciones = true;
        } else {
            notificaciones = false;
        }



        if ((nombreEmpresa.isEmpty() || nombreGerente.isEmpty() || codigo.isEmpty() || confirmarCodigo.isEmpty()) || !(codigo.equals(confirmarCodigo))){
            //Toast de que Todos los campos obligatorios y contraseñas coincidentes
            //Log.i("myapp", "NO informa bien los campos");
            Toast.makeText(getBaseContext(), "Todos los campos son obligatorios y el código debe coincidir", Toast.LENGTH_LONG).show();
        }else{
            //Meterlo en una clase empresa y enviarlo a la activity 'CodigoEmpresa'
            //Log.i("myapp", "Campos OK");
            Empresa datosEmpresa = new Empresa(nombreEmpresa, nombreGerente, codigo, notificaciones, LE);
            Intent i = new Intent(this,CodigoEmpresa.class);
            i.putExtra("datosE", datosEmpresa);
            startActivity(i);
        }

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

}
