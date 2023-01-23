package it.unibo.sd.beccacino;

//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import it.unibo.sd.beccacino.rabbitmq.RabbitMQManager;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        LoggerFactory.getLogger("ds-app").info("CAZZODUROCONTROILMURO");
        MongoDatabase db;
        MongoClient clientMongo = MongoClients.create(System.getenv("MONGODB"));
        //MongoClient clientMongo = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        db = clientMongo.getDatabase("local");
        System.out.println("DB Name: "+db.getName());
        db.getCollection("testing").insertOne(new Document());
        System.out.println("Documents count: "+db.getCollection("testing").countDocuments());

        /*Listener listener = new Listener("listener");
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

        RabbitMQManager rabbitMQManager = new RabbitMQManager();

        Server server = new Server(rabbitMQManager);
        Client client = new Client(rabbitMQManager);

        try {
            client.run();
            server.run();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }*/


    }
}
