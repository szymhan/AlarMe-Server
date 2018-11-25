package pl.szymonhanzel.alarmeserver.services;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.sun.media.jfxmedia.logging.Logger;
import pl.szymonhanzel.alarmeserver.models.Alarm;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FirestoreDatabaseService {

    public static final String TAG = "FireStoreDatabaseService";
    private static Firestore db;
    private static final FirestoreDatabaseService instance = new FirestoreDatabaseService();
    private static final long SECONDS = 120l;
    private static final String USERS_COLLECTION = "users";

    EventListener<QuerySnapshot> alarmsListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirestoreException e) {
            if (e != null) {
                System.err.println("Listen failed:" + e);
                return;
            }
            if(queryDocumentSnapshots!= null && !queryDocumentSnapshots.isEmpty()){
                for(DocumentChange dc: queryDocumentSnapshots.getDocumentChanges()){
                    Alarm alarm = null;
                    switch (dc.getType()) {
                        case ADDED:
                            if(FirebaseDataAnalyzer.validateMap(dc.getDocument().getData())){
                               //TODO: mapowanie danych na obiekt reprezentujacy alarm
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
            Logger.logMsg(Logger.ERROR, "Cannot found the credentials JSON file. Please check if the file physically exists.");
            return false;
        } catch (Exception e) {
            Logger.logMsg(Logger.ERROR, "Unable to set Google Firebase connection.");
            return false;
        }
    }

    public void setListener() {
        db.collection("alarms").addSnapshotListener(alarmsListener);
    }

    public List<QueryDocumentSnapshot> getDocuments(String collection){

        try{
            // asynchronously retrieve all users
            ApiFuture<QuerySnapshot> query = db.collection(collection).get();
            // ...
            // query.get() blocks on response
            QuerySnapshot querySnapshot = query.get();
            return  querySnapshot.getDocuments();
        } catch (InterruptedException | ExecutionException ee){
            Logger.logMsg(Logger.ERROR,"Unable to execute query.");
            return Collections.emptyList();
        }
        }

    public List<QueryDocumentSnapshot> getActiveUsers(){
        try{
            // asynchronously retrieve all users
            long now = Timestamp.now().getSeconds() - SECONDS ;
            Timestamp timestampToCompare = Timestamp.ofTimeSecondsAndNanos(now,0);
            ApiFuture<QuerySnapshot> query = db.collection(USERS_COLLECTION).whereGreaterThan("timestamp",timestampToCompare).get();
            // ...
            // query.get() blocks on response
            QuerySnapshot querySnapshot = query.get();
            return  querySnapshot.getDocuments();
        } catch (InterruptedException | ExecutionException ee){
            Logger.logMsg(Logger.ERROR,"Unable to execute query.");
            return Collections.emptyList();
        }

    }


}
