package net.wandroid.answer.bot;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class that handles the communication with the AI bot with JSON
 */
public class JsonBot {
    private final static String API_AUTHORITY = "www.personalityforge.com";

    public static final String API_KEY = "apiKey";
    public static final String MESSAGE = "message";
    public static final String CHAT_BOT_ID = "chatBotID";
    public static final String EXTERNAL_ID = "externalID";

    /**
     * The external id is the id of the person that the bot is talking to,so that the bot
     * can recognize between different users. This should in the future be something unique, preferable
     * the phone number to the person that the reply is going to be sms:ed to
     */
    public static final String EXTERNAL_ID_VALUE = "2";

    /**
     * Sends a message to the AI and returns the response JSON as a string
     * Must be called on a background thread
     *
     * @param message   the text message
     * @param apiKey    apiKey
     * @param chatbotId the chatbot id (see www.personalityforge.com for more information)
     * @return The AI JSON reply
     * @throws IOException
     * @throws HttpException
     */
    public String connect(String message, String apiKey, String chatbotId) throws IOException, HttpException {
        Uri uri = new Uri.Builder().scheme("http").authority(API_AUTHORITY)
                .path("api/chat")
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(MESSAGE, message)
                .appendQueryParameter(CHAT_BOT_ID, chatbotId)
                .appendQueryParameter(EXTERNAL_ID, EXTERNAL_ID_VALUE)
                        //TODO: Possibility to also set following key pair to the AI:firstName,lastName,gender
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

    /**
     * Sends a message to the AI and returns the response
     * Must be called on a background thread
     *
     * @param message   the text message
     * @param apikey    apiKey
     * @param chatbotId the chatbot id (see www.personalityforge.com for more information)
     * @return the response parsed to a BotResposeJson class
     * @throws IOException
     * @throws HttpException
     */
    public BotResposeJson getResponse(String message, String apikey, String chatbotId) throws IOException, HttpException {
        String response = connect(message, apikey, chatbotId);
        Log.d("", "Auto bot answer is:" + response);

        // the reply is not pure JSON, it starts with a HTML tag
        // but there's no documentation of this.
        if (!response.startsWith("{") && response.contains("{")) {
            //skip to the JSON code
            response = response.substring(response.indexOf('{'));
        }
        Gson gson = new Gson();
        return gson.fromJson(response, BotResposeJson.class);
    }

    @SuppressWarnings("unused")
    public static class BotResposeJson {
        private int success;
        private String errorMessage;
        private Message message;

        private static class Message {
            private String chatBotName;
            private int chatBotID;
            private String message;
            private String emotion;
        }

        @Override
        public String toString() {
            if (message == null) {
                return null;
            }
            return message.message;
        }
    }

}
