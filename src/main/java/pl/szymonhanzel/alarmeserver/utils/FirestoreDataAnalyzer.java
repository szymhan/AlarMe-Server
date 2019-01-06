package pl.szymonhanzel.alarmeserver.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szymonhanzel.alarmeserver.models.Alarm;
import pl.szymonhanzel.alarmeserver.models.User;
import pl.szymonhanzel.alarmeserver.services.FirestoreDatabaseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class FirestoreDataAnalyzer {

    @Autowired
    private FirestoreDatabaseService firestoreDatabaseService;
    @Autowired
    private DistanceCalculator distanceCalculator;

    private static final String ALTITUDE = "altitude";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String VEHICLE = "vehicleType";
    private static final String TIMESTAMP = "timestamp";
    private static final String TOKEN = "token";

    private static final double MAXIMUM_DISTANCE_TO_NOTIFY = 1000;

    /**
     * Metoda sprawdzająca przychodzące dane, czy dokument posiada wymagane pola potrzebne do utworzenia obiektu klasy Alarm
     * @param map
     * @return
     */
    public  boolean validateMap(Map<String,Object> map) {
        if(map.containsKey(ALTITUDE)
                && map.containsKey(VEHICLE)
                && map.containsKey(TIMESTAMP)
                && map.containsKey(LONGITUDE)
                && map.containsKey(LATITUDE)){
                return true;
        } else {
            return false;
        }
    }


    /**
     * Metoda validująca, czy dokument w kolekcji użytkowników posiada wszystkie wymagane pola
     * @param map - mapa wszystkich kluczów danego użytkownika
     * @return
     */
    public  boolean validateUser(Map<String,Object> map) {
        if (map.containsKey(TOKEN)
        && map.containsKey(TIMESTAMP)
        && map.containsKey(LATITUDE)
        && map.containsKey(LONGITUDE)
        && map.containsKey(ALTITUDE)){
            return true;
        } else {
            return false;
        }
    }

    /**Tutaj odbywa się obliczanie pomiędzy alarmem, który powinnien zostać wysłany a urządzeniami, które były aktywne
     * przez ostatnie 2 minuty. Za pomocą metody getActiveUsers() pobieramy te wszystkie urządzenia, następnie
     * iterujemy po tej liście i obliczamy dystans pomiędzy alarmem a urządzeniem
     *
     * @param alarm - zgłoszony alarm
     * @return - lista tokenów urządzeń, które mają zostać powiadomione
     */
    public  List<String> findDevicesToNotify(Alarm alarm){
        List <String> tokenList = new ArrayList<>();
        List <User> listOfActiveUsers = firestoreDatabaseService.getActiveUsers();
        for (User user : listOfActiveUsers){
           double distance = distanceCalculator.distance(
                       user.getLatitude(),
                       alarm.getLatitude(),
                       user.getLongitude(),
                       alarm.getLongitude(),
                       user.getAltitude(),
                       alarm.getAltitude()

               );
           if(distance < MAXIMUM_DISTANCE_TO_NOTIFY){
                tokenList.add(user.getToken());
           }
        }
        return tokenList;
    }


}
