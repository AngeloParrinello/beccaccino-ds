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
        System.out.println("ooooooooooo");
        MongoClient client = MongoClients.create(System.getenv("MONGODB"));
        //MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        System.out.println("aaaaaaaaaa");
        db = client.getDatabase("local");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:"+db.getName());
        db.getCollection("fica").insertOne(new Document());
        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"+db.getCollection("fica").countDocuments());


        Listener listener = new Listener("listener");
        listener.start();
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
