package es.frios.pedidos;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpManager {

    public static InputStream obtenerContenido(String url) throws IOException {
        //REALIZAR LA PETICIÓN AL SERVIDOR Y OBTENER EL JSON CON LOS DATOS
        //CREAMOS UN OBJETO DE TIPO HTTPCLIENT QUE NOS PERMITE CONECTARNOS
        //MEDIANTE PETICIONES HTTP
        HttpClient httpClient = new DefaultHttpClient();
        //OBJETO DE TIPO HTTPPOST PERMITE QUE ENVIEMOS UNA PETICIÓN DE TIPO
        //POST A UNA URL ESPECIFICA
        HttpPost httpPost = new HttpPost(url);
        //Un objeto encargado de recibir la respuesta del servidor
        HttpResponse response = httpClient.execute(httpPost);
        //En respuesta recibir muchas cosas. La cabecera, fecha, hora, ip's..
        //pero a nosotros nos interesa el contenido...vamos a extraerlo.
        HttpEntity entity = response.getEntity();
        InputStream contenido = entity.getContent();//contenido de la petición
        return  contenido;
    }

    public static String getStringFromInputStream(InputStream is) throws IOException{
        //Código para pasar un InputStream a un String
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        //Leer del buffer
        String linea;
        while((linea = bf.readLine())!=null){
            sb.append(linea);
        }
        return sb.toString();//develovemos el String
    }

    /*public static do double calcularImporte(int id_P){
        double importe=0;

        return importe;
    }
    */

}
