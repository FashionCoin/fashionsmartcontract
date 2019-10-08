package fashion.coin.wallet.back.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

@Service
public class MessagingService {

    Logger logger = LoggerFactory.getLogger(MessagingService.class);

    private Gson gson;

    private static final String PROJECT_ID = "fshn-wallet";
    private static final String BASE_URL = "https://fcm.googleapis.com";
//    private static final String FCM_SEND_ENDPOINT = "/v1/projects/%s/messages:send";
    private static final String FCM_SEND_ENDPOINT = "/v1/projects/%s/messages:send";
    private static final String URL = BASE_URL + FCM_SEND_ENDPOINT;

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = {MESSAGING_SCOPE};

    public static final String MESSAGE_KEY = "message";
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String TOPIC = "topic";

    private static final String GOOGLE_CREDENTIALS_PATH = "/opt/walletback/firebase/fshn-wallet-firebase-adminsdk-hlfp2-d8e79e620f.json";


    public String sendNotification(String title, String body, String to) {
        JsonObject message = buildMessage(title, body, to);
        return sendMessage(message, PROJECT_ID);
    }

    private JsonObject buildMessage(String title, String body, String to) {
        JsonObject jNotificationMessage = buildNotificationMessage(title, body, to);

        JsonObject messagePayload = jNotificationMessage.get(MESSAGE_KEY).getAsJsonObject();

        messagePayload.add("data", buildDataOverridePayload(title, body));

        messagePayload.add("android", buildAndroidOverridePayload());

        JsonObject apnsPayload = new JsonObject();
        apnsPayload.add("headers", buildApnsHeadersOverridePayload());
        apnsPayload.add("payload", buildApsOverridePayload(title, body));

        messagePayload.add("apns", apnsPayload); // Apple Push Notification Service specific options.

       jNotificationMessage.add(MESSAGE_KEY, messagePayload);

       logger.info(gson.toJson(jNotificationMessage) + "\n");

        return jNotificationMessage;
    }

    private JsonObject buildNotificationMessage(String title, String body, String to) {
        JsonObject jNotification = new JsonObject();
        jNotification.addProperty(TITLE, title);
        jNotification.addProperty(BODY, body);

        JsonObject jMessage = new JsonObject();
//        jMessage.add("notification", jNotification);
        jMessage.addProperty(TOPIC, to);

        JsonObject jFcm = new JsonObject();
        jFcm.add(MESSAGE_KEY, jMessage);
//        jFcm.addProperty("validate_only", true); // Flag for testing the request without actually delivering the message.

        return jFcm;
    }

    private JsonElement buildDataOverridePayload(String title, String body) {
        JsonObject jData = new JsonObject();
        jData.addProperty(TITLE, title);
        jData.addProperty(BODY, body);
//        jData.addProperty("icon", BODY);

        return jData;
    }

    private JsonObject buildAndroidOverridePayload() {
        JsonObject androidNotification = new JsonObject();
        androidNotification.addProperty("click_action", "android.intent.action.MAIN");

        JsonObject androidNotificationPayload = new JsonObject();
//        androidNotificationPayload.add("notification", androidNotification);

        return androidNotificationPayload;
    }

    private JsonObject buildApnsHeadersOverridePayload() {
        JsonObject apnsHeaders = new JsonObject();
        apnsHeaders.addProperty("apns-priority", "10");

        return apnsHeaders;
    }

    private JsonObject buildApsOverridePayload(String title, String body) {
        JsonObject badgePayload = new JsonObject();
        badgePayload.addProperty("badge", 1);
        badgePayload.add("alert", buildDataOverridePayload(title,body));
        badgePayload.addProperty("sound", "default");

        JsonObject apsPayload = new JsonObject();
        apsPayload.add("aps", badgePayload);

        return apsPayload;
    }



    private String sendMessage(JsonObject fcmMessage, String projectId){
        try {
            HttpURLConnection connection = getConnection(projectId);
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(fcmMessage.toString());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            String response;

            if (responseCode == 200) {
                response = inputstreamToString(connection.getInputStream());
             logger.info("Message sent to Firebase for delivery, response:");
                logger.info(response);
            } else {
                logger.error("Unable to send message to Firebase:");
                response = inputstreamToString(connection.getErrorStream());
                logger.error(response);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private HttpURLConnection getConnection(String projectId) throws IOException {
        // [START use_access_token]
        URL url = new URL(String.format(URL, projectId));
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
        // [END use_access_token]
    }

    private String getAccessToken() throws IOException {

        GoogleCredential googleCredential = GoogleCredential
                .fromStream(new FileInputStream(GOOGLE_CREDENTIALS_PATH))
                .createScoped(Arrays.asList(SCOPES));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }

    private String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
