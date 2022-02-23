package it.unibo.sd.beccacino;

//import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {

        MongoDatabase db;
        MongoClient client = MongoClients.create(System.getenv("MONGODB"));
        //MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = client.getDatabase("local");
        System.out.println("DB Name: "+db.getName());
        db.getCollection("testing").insertOne(new Document());
        System.out.println("Documents count: "+db.getCollection("testing").countDocuments());


        Listener listener = new Listener("listener");
        listener.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Sender sender = new Sender("sender", "test message");
        sender.start();
        try {
            sender.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        try {
            listener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
