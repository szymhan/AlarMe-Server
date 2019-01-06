package pl.szymonhanzel.alarmeserver;


import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pl.szymonhanzel.alarmeserver.services.FirestoreDatabaseService;

import java.util.List;


@SpringBootApplication
public class App {

   @Autowired
   private FirestoreDatabaseService firestoreDatabaseService;

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
        App app = context.getBean(App.class);
        SpringApplication.run(App.class,args);
        app.initalizeApp();

    }

    private void initalizeApp() {
        System.out.println("Initalizing app...");
        if(firestoreDatabaseService.setConnection()){
            System.out.println("Connected successfully");

            List<QueryDocumentSnapshot> listOfElements = firestoreDatabaseService.getDocuments("alarms");
            System.out.println(listOfElements.size());

            firestoreDatabaseService.setListener();
        } else {
            System.out.println("Connection failed");
            System.exit(0);
        }
    }


}
