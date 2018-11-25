package pl.szymonhanzel.alarmeserver.services;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import pl.szymonhanzel.alarmeserver.models.Alarm;

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

    public static void findDevices(Alarm alarm){
        List <QueryDocumentSnapshot> listOfActiveUsers = FirestoreDatabaseService.getInstance().getActiveUsers();

    }

   /* public static boolean findIfDevicesToNotify(Alarm alarm){

    }*/
}
