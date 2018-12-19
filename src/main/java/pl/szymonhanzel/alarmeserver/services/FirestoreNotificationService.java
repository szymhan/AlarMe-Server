package pl.szymonhanzel.alarmeserver.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;
import pl.szymonhanzel.alarmeserver.models.Alarm;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class FirestoreNotificationService {

    private static final String ALTITUDE = "altitude";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String VEHICLE = "vehicleType";
    private static final Logger logger = Logger.getAnonymousLogger();
    public static final String TAG = "FirestoreNotificationService";


    /**
     * Metoda odpowiadająca za powiadomienie wszystkich urządzeń urządzeń, które zostały
     * zwalidowane jako do oznaczenia
     * @param userTokens
     * @param alarm
     * @return
     */
    public  boolean notifyUsers(List<String> userTokens, Alarm alarm){

        if(userTokens.size()> 0){
            for (String token : userTokens) {
                sendMessageToUser(token,alarm);
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * Metoda służąca do powiadomienia pojedynczego użytkownika
     * @param token - token użytkownika do powiadomienia
     * @param alarm - obiekt przechowujący informacje potrzebne do wyświetlenia alarmu. Jego
     *              dane są przekazywane do wiadomości (message) za pomocą metod putData(klucz,wartość)
     * @return - zwraca wartość logiczną informującą, czy udało się wysłać wiadomość
     */
    private  boolean sendMessageToUser (String token,Alarm alarm){
        // See documentation on defining a message payload.
        Message message = Message.builder()
                .putData(VEHICLE, alarm.getVehicleType())
                .putData(LONGITUDE, String.valueOf(alarm.getLongitude()))
                .putData(LATITUDE,String.valueOf(alarm.getLatitude()))
                .putData(ALTITUDE,String.valueOf(alarm.getAltitude()))
                .setToken(token)
                .build();
        try {
            // Send a message to the device corresponding to the provided
            // registration token.
            String response = FirebaseMessaging.getInstance().send(message);
            // Response is a message ID string.
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException fme){
            logger.log(Level.ALL,TAG + ": Operacja wysłania Cloud Message nie powiodła się.");
            System.out.println("Message didn't sent ");
            return false;
        }

        return true;
    }


}
