package pl.szymonhanzel.alarmeserver.services;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import pl.szymonhanzel.alarmeserver.models.Alarm;
import pl.szymonhanzel.alarmeserver.models.User;


import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FirestoreDatabaseService {

    public static final String TAG = "FireStoreDatabaseService";
    private static Firestore db;
    private static final FirestoreDatabaseService instance = new FirestoreDatabaseService();
    private static final long SECONDS = 120l;
    private static final String USERS_COLLECTION = "users";
    private static final Logger  logger = Logger.getAnonymousLogger();

    private EventListener<QuerySnapshot> alarmsListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirestoreException e) {
            if (e != null) {
                System.err.println("Listen failed:" + e);
                return;
            }
            if(queryDocumentSnapshots!= null && !queryDocumentSnapshots.isEmpty()){
                for(DocumentChange dc: queryDocumentSnapshots.getDocumentChanges()){

                    switch (dc.getType()) {
                        case ADDED:
                            
                            Alarm alarm = new Alarm();
                            if(FirestoreDataAnalyzer.validateMap(dc.getDocument().getData())){
                                QueryDocumentSnapshot ds = dc.getDocument();
                                try {
                                    alarm.setVehicleType(String.valueOf(ds.get("vehicleType")));
                                    alarm.setTimestamp(ds.getTimestamp("timestamp"));
                                    alarm.setAltitude(ds.getDouble("altitude"));
                                    alarm.setLatitude(ds.getDouble("latitude"));
                                    alarm.setLongitude(ds.getDouble("longitude"));
                                    //TODO: wyszukanie urzadzeń do powiadomienia
                                   List<String> tokensToNotify =  FirestoreDataAnalyzer.findDevicesToNotify(alarm);
                                    //TODO: powiadomienie urządzeń
                                    FirestoreNotificationService.notifyUsers(tokensToNotify,alarm);
                                } catch (NullPointerException npe) {
                                    logger.log(Level.ALL, "Mapping from DocumentSnapshot to Alarm failed. " +
                                            "Probably something in Snapshot is missing.");
                                }

                            }
                            break;
                    }
                }
            }
        }
    };

    private FirestoreDatabaseService() {
    }

    public static FirestoreDatabaseService getInstance() {
        return instance;
    }


    /**
     * metoda zestawiająca połączenie z bazą Google Cloud Platform - Firestore
     * @return
     */
    public  boolean setConnection() {

        try {
            InputStream serviceAccount = new FileInputStream("alarme-hanzel-credentials.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
           /* FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();*/
            FirestoreOptions options =
                    FirestoreOptions
                            .newBuilder()
                            .setCredentials(credentials)
                            .setTimestampsInSnapshotsEnabled(true).build();
            db = options.getService();
            return true;
        } catch (IOException nsfe) {
            logger.log(Level.ALL, "Cannot found the credentials JSON file. Please check if the file physically exists.");
            return false;
        } catch (Exception e) {
            logger.log(Level.ALL, "Unable to set Google Firebase connection.");
            return false;
        }
    }


    public void setListener() {
        db.collection("alarms").addSnapshotListener(alarmsListener);
    }

    /**
     *
     * @param collection - nazwa kolekcji, dla której mają zostać pobrane wszystkie dokumenty
     * @return - lista pobranych dokumentów
     */
    public List<QueryDocumentSnapshot> getDocuments(String collection){

        try{
            // asynchronously retrieve all users
            ApiFuture<QuerySnapshot> query = db.collection(collection).get();
            // ...
            // query.get() blocks on response
            QuerySnapshot querySnapshot = query.get();
            return  querySnapshot.getDocuments();
        } catch (InterruptedException | ExecutionException ee){
            logger.log(Level.ALL,"Unable to execute query.");
            return Collections.emptyList();
        }
        }

    /**
     * Metoda zwracająca listę wszystkich użytkowników, którzy byli aktywni w przeciągu ostatnich
     * 120 sekund.
     * @return
     */
    public List<User> getActiveUsers(){
        try{
            // asynchronously retrieve all users
            // aktualny Timestamp pomniejszony o 120 sekund
            long now = Timestamp.now().getSeconds() - SECONDS ;
            Timestamp timestampToCompare = Timestamp.ofTimeSecondsAndNanos(now,0);
            ApiFuture<QuerySnapshot> query = db.collection(USERS_COLLECTION).whereGreaterThan("timestamp",timestampToCompare).get();
            // ...
            // query.get() blocks on response
            QuerySnapshot querySnapshot = query.get();
            return  convertToUserList(querySnapshot.getDocuments());
        } catch (InterruptedException | ExecutionException ee){
            Logger.getAnonymousLogger().log(Level.ALL,"Unable to execute query.");
            return Collections.emptyList();
        }

    }


    /**
     * Metoda zamieniająca listę obiektów zwróconych z zapytania na listę
     * obiektów posiadających prawidłowe pola (klasa User).
     * @param documentList - lista obiektów zwróconych z zapytania
     * @return - lista użytkowników
     */
    private List<User> convertToUserList(List<QueryDocumentSnapshot> documentList) {
        List<User> returnList = new ArrayList<>();
        if(documentList.size() >0){
            for (DocumentSnapshot dc: documentList) {
                if(FirestoreDataAnalyzer.validateUser(dc.getData())){
                    try {
                        User user = new User();
                        user.setToken(dc.getString("token"));
                        user.setLatitude(dc.getDouble("latitude"));
                        user.setLongitude(dc.getDouble("longitude"));
                        user.setAltitude(dc.getDouble("altitude"));
                        user.setTimestamp(dc.getTimestamp("timestamp"));
                        returnList.add(user);
                    } catch (Exception e){
                        logger.log(Level.ALL, TAG + ": Cannot add data to User List");
                    }
                }
            }
            return returnList;
        } else {
            return Collections.emptyList();
        }
    }


}
