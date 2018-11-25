package pl.szymonhanzel.alarmeserver;


import pl.szymonhanzel.alarmeserver.services.FirestoreDatabaseService;

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
    }
}
