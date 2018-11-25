package pl.szymonhanzel.alarmeserver.services;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import pl.szymonhanzel.alarmeserver.models.Alarm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FirebaseDataAnalyzer {

    private static final List<String> VEHICLE_TYPES = Arrays.asList("Straż pożarna", "Policja", "Pogotowie","Transport krwi");

    /**
     * Metoda sprawdzająca przychodzące dane, czy dokument posiada wymagane pola potrzebne do utworzenia obiektu klasy Alarm
     * @param map
     * @return
     */


    public static boolean validateMap(Map<String,Object> map) {
        if(map.containsKey("coordinates") && map.containsKey("vehicleType") && map.containsKey("timestamp")){
            if(VEHICLE_TYPES.contains(String.valueOf(map.get("coordinates")))){
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
