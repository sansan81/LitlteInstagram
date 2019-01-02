package com.whoami.litlteinstagram.lib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class InternetHelper
{
    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_METHOD_GET = "GET";

    private static String urlEncode(String url)
    {
        String urlStr = null;
        try
        {
            urlStr = URLEncoder.encode(url, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("InternetHelper", "urlEncode method error: " + e.getMessage());
        }
        return urlStr;
    }

    private static ArrayList<KeyValue> urlEncodeDataValues(ArrayList<KeyValue> data)
    {
        ArrayList<KeyValue> encodeData = new ArrayList<>();
        for (KeyValue kv : data){
            String value = kv.getValue().toString();
            value = InternetHelper.urlEncode(value);
            kv.setValue(value);
            encodeData.add(kv);
        }
        return encodeData;
    }

    private static String retrieveResponseString(HttpURLConnection connection) throws Exception
    {
        InputStream responseStream = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(responseStream);
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder strBuilder = new StringBuilder();
        while ((line = br.readLine()) != null)
        {
            strBuilder.append(line);
        }
        br.close();
        return strBuilder.toString();
    }

    public static String sendHTTPRequest(String method, String urlS, ArrayList<KeyValue> data) throws Exception
    {
        method = method.toUpperCase();
        if(method.equals(InternetHelper.REQUEST_METHOD_GET))
        {
            data = InternetHelper.urlEncodeDataValues(data);
            urlS = (urlS + "?" + KeyValue.makeURIFormat(data));
        }

        URL url = new URL(urlS);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");

        if(method.equals(InternetHelper.REQUEST_METHOD_POST))
        {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // POST datanya
            String dataStr = KeyValue.makeURIFormat(data);

            // Tambahkan POST data ke objek connection
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(dataStr);
            outputStream.flush();
            outputStream.close();
        }

        if(connection.getResponseCode() == -1)
        {
            Log.e("InternetHelper", "Response Code is -1");
            throw new Exception("Cannot connect to host " +
                    connection.getURL().toString());
        }
        String response = InternetHelper.retrieveResponseString(connection);
        return response;
    }

    public static String uploadFiles(String urlString, FormData data) throws Exception
    {
        ArrayList<KeyValue> allFileData = data.getData(FormData.CONTENT_TYPE_MULTIPART_FILE);
        ArrayList<KeyValue> allPlainData = data.getData(FormData.CONTENT_TYPE_PLAIN_TEXT);
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "InternetHelper_uploadFiles_at_" + System.currentTimeMillis();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3000);
        connection.setRequestMethod(InternetHelper.REQUEST_METHOD_POST);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        // Tambahkan data untuk dikirim
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        for(KeyValue fileData : allFileData)
        {
            String attachmentName = fileData.getKey();
            String attachmentFileName = data.fileNameFor(fileData);
            outputStream.writeBytes(twoHyphens + boundary + crlf);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
            outputStream.writeBytes(crlf);
            byte[] fileBytes = (byte[]) fileData.getValue();
            outputStream.write(fileBytes);
            outputStream.writeBytes(crlf);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        }

        for(KeyValue plainData : allPlainData)
        {
            outputStream.writeBytes(twoHyphens + boundary + crlf);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + plainData.getKey() + "\"" + crlf);
            outputStream.writeBytes("Content-Type: text/plain" + crlf);
            outputStream.writeBytes(crlf);
            outputStream.writeBytes(plainData.getValue().toString());
            outputStream.writeBytes(crlf);
        }
        outputStream.flush();
        outputStream.close();
        int responseCode = connection.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK)
        {
            throw new Exception("Failed (Response Code: " + responseCode +") to establish connection with host " + connection.getURL().toString());
        }
        String response = InternetHelper.retrieveResponseString(connection);
        return response;
    }
}
