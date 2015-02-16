package net.wandroid.answer.bot;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class BotJson {
    private final static String API_AUTHORITY = "www.personalityforge.com";

    public String connect(String message,String apiKey,String chatbotId) throws ClientProtocolException, IOException, HttpException {
        Uri uri = new Uri.Builder().scheme("http").authority(API_AUTHORITY)
                .path("api/chat")
                .appendQueryParameter("apiKey", apiKey)
                .appendQueryParameter("message", message)
                .appendQueryParameter("chatBotID", chatbotId)
                .appendQueryParameter("externalID", "2")
                //TODO: send as parameters
//                .appendQueryParameter("firstName", "Hans")
//                .appendQueryParameter("lastName", "Lind")
//                .appendQueryParameter("gender", "m")
                .build();

        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri.toString());
        HttpResponse response = client.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            InputStream inStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            inStream.close();
            return sb.toString();
        } else {
            throw new HttpException("Could not connect:" + statusCode);
        }
    }

    public BotResposeJson getResponse(String message,String apikey,String chatbotId) throws ClientProtocolException, IOException, HttpException{
        String response=connect(message,apikey,chatbotId);
        Log.d("", "Auto bot answer is:"+response);
        if(!response.startsWith("{") && response.contains("{")){
            response=response.substring(response.indexOf('{'));
        }
        Gson gson=new Gson();
        return gson.fromJson(response, BotResposeJson.class);
    }

    @SuppressWarnings("unused")
    public static class BotResposeJson{
        private int success;
        private String errorMessage;
        private Message message;

        private static class Message{
            private String chatBotName;
            private int chatBotID;
            private String message;
            private String emotion;
        }
        @Override
        public String toString() {
            if(message==null){
                return null;
            }
            return message.message;
        }
    }

}
