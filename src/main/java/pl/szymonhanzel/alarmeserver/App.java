package pl.szymonhanzel.alarmeserver;


import com.google.cloud.firestore.QueryDocumentSnapshot;
import pl.szymonhanzel.alarmeserver.services.FirestoreDatabaseService;

import java.util.List;

public class App {

    public static void main(String[] args) {
        initalizeApp();
    }

    private static void initalizeApp() {
        System.out.println("Initalizing app...");
        if(FirestoreDatabaseService.getInstance().setConnection()){
            System.out.println("Connected successfully");
        } else {
            System.out.println("Connection failed");
        }

        List<QueryDocumentSnapshot> listOfElements = FirestoreDatabaseService.getInstance().getDocuments("alarms");
        System.out.println(listOfElements.size());

        FirestoreDatabaseService.getInstance().setListener();
    }
}
