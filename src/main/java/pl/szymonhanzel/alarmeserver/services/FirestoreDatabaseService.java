package pl.szymonhanzel.alarmeserver.services;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.sun.media.jfxmedia.logging.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FirestoreDatabaseService {

    public static final String TAG = "FireStoreDatabaseService";
    private static Firestore db;
    private static FirestoreDatabaseService instance = new FirestoreDatabaseService();

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


}
