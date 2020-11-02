package com.example.codigo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HiloConexionImag extends Thread {

    private static final String URI = "https://api.qrserver.com/v1/create-qr-code/?";
    private Handler handler;
    private String cadena;

    public HiloConexionImag(Handler handler, String cadena) {
        this.handler = handler;
        this.cadena = cadena;
    }

    @Override
    public void run() {
        ejecutarPeticion();
    }

    private void ejecutarPeticion() {

        HttpURLConnection urlConnection = null;
        String resul = "";
        try {
            URL mUrl = new URL("http://api.qrserver.com/v1/create-qr-code/?data=" + cadena + "&size=115x131");
            urlConnection = (HttpURLConnection) mUrl.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();
            int response = urlConnection.getResponseCode();
            InputStream instream = urlConnection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            try {
                while ((len = instream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] b = baos.toByteArray();
            urlConnection.disconnect();

            Message msg = new Message();
            Bundle datos = new Bundle();
            datos.putByteArray("MENSAJE", b);
            msg.setData(datos);
            this.handler.sendMessage(msg);
        } catch (Exception e) {
            return;
        }
    }
}
