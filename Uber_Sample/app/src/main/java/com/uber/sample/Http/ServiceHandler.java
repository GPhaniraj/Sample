package com.uber.sample.Http;


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class ServiceHandler {

    /**
     * Generic method to execute all the IO operations, helps to connect to
     * given url and get response.
     *
     * @param url url to be connected.
     * @return Raw response from server String
     */
    public static String processGetRequest(String url, int timeout){

        HttpURLConnection urlConnection = null;
        String results = null;
        try
        {
            URL urlCopy = new URL(url); //Assuming the given path, this can be anything
            urlConnection =  (HttpURLConnection) urlCopy.openConnection();  //Open the connection with the DB

            /*** Online Authorization **/
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setReadTimeout(timeout);
            urlConnection.connect();
            int status = urlConnection.getResponseCode();

            switch (status){

                case 201:
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null)
                    {
                        sb.append(line+"\n");
                    }
                    br.close();
                    Log.e("ServiceResponse= ", sb.toString());
                    return sb.toString();

            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return "";
    }


    public static String processPostRequest(String postCallUrl, JSONObject jsonParam) throws Exception {

        String contentAsString = "";
        InputStream inputStream;
        URL url = new URL(postCallUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        /*** Online Authorization **/
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        //connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Content-Type", "application/json");
        //connection.setRequestProperty("Accept", "application/json");
        //connection.setRequestProperty("charset", "utf-8");
        //connection.setRequestProperty("Authorization", "Basic");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
       // connection.setUseCaches(false);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
        System.out.println("ServiceResponse-->Sending Json Params="+jsonParam.toString());
        wr.writeBytes(jsonParam.toString());
        wr.flush();
        wr.close();

        //do somehting with response
        int responseCode = connection.getResponseCode();
        if(responseCode >= HttpURLConnection.HTTP_BAD_REQUEST){
            inputStream = connection.getErrorStream();
            contentAsString = readStream(inputStream);
            inputStream.close();
            System.out.println("ServiceResponse=" + contentAsString + "---" + responseCode);
        }else{
            inputStream = connection.getInputStream();
            contentAsString = readStream(inputStream);
            inputStream.close();
            System.out.println("ServiceResponse=" + contentAsString + "---" + responseCode);
        }

       /* //do somehting with response
        InputStream is = connection.getInputStream();
        String contentAsString = convertInputStreamToString(is);
        System.out.println("ServiceResponse=" + contentAsString + "---" + responseCode);
        is.close();*/

        if (connection != null) {
            connection.disconnect();
        }

        return contentAsString;

    }

    public static String sendAudioFileToServer(String postCallUrl, String fileName) throws Exception {

        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(postCallUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content_Type", "audio/vnd.wav");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setConnectTimeout(20 * 60 * 1000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out,fileName);
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } catch (IOException ioe) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }


        return result;

    }


    private static void writeStream(OutputStream outputStream, String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        try {
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                outputStream.write(buffer);
            }
            outputStream.flush();
        } finally {
            fileInputStream.close();
        }
    }

    private static String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb.toString();

    }



    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
//https://github.com/hoomi/WaveRecorderAndroid/blob/master/app/src/main/java/uk/co/o2/android/google/voicerecognizer/AudioRecordTest.java