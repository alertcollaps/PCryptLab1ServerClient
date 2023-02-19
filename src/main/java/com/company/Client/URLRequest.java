package com.company.Client;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class URLRequest {
    private final String url = "http://localhost:8080";
    HttpURLConnection connection = null;

    public URLRequest sendPOST(String urlInput, String... data){
        urlInput = url + urlInput;
        OutputStream stream = null;



        try {

            URL obj = new URL(urlInput);

            connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");

            if (data.length >= 1){
                connection.setDoOutput(true);
                stream = connection.getOutputStream();

                //

                byte[] out = data[0].getBytes(StandardCharsets.US_ASCII);

                //out = Base64.getEncoder().encode(out);

                String preOut = new String(out);


                out = preOut.getBytes(StandardCharsets.US_ASCII);

                stream.write(out, 0, out.length);
            }


        } catch (IOException ex){
            ex.printStackTrace();
        } finally {
            if (stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    public String getResponse(){
        if (connection == null){
            return "";
        }

        StringBuffer response = new StringBuffer();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }


        return response.toString();
    }



}
