package pl.szymonhanzel.alarmeserver.services;

import pl.szymonhanzel.alarmeserver.models.Alarm;
import pl.szymonhanzel.alarmeserver.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FirestoreDataAnalyzer {

    private static final List<String> VEHICLE_TYPES = Arrays.asList("Straż pożarna", "Policja", "Pogotowie","Transport krwi");
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
    public static boolean validateMap(Map<String,Object> map) {
        if(map.containsKey(ALTITUDE)
                && map.containsKey(VEHICLE)
                && map.containsKey(TIMESTAMP)
                && map.containsKey(LONGITUDE)
                && map.containsKey(LATITUDE)){
            if(VEHICLE_TYPES.contains(String.valueOf(map.get(VEHICLE)))){
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }


    /**
     * Metoda validująca, czy dokument w kolekcji użytkowników posiada wszystkie wymagane pola
     * @param map - mapa wszystkich kluczów danego użytkownika
     * @return
     */
    public static boolean validateUser(Map<String,Object> map) {
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
    public static List<String> findDevicesToNotify(Alarm alarm){
        List <String> tokenList = new ArrayList<>();
        List <User> listOfActiveUsers = FirestoreDatabaseService.getInstance().getActiveUsers();
        for (User user : listOfActiveUsers){
           double distance = DistanceCalculator.distance(
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
