package it.unibo.sd.beccacino;

import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
// import com.mongodb.client.MongoClient;
// import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;

public class DBManager {
    private final MongoDatabase db;
    
    public DBManager() {
        // Creation of mongoclient to run local. if you wish to run it inside a container use:
        // MongoClient client = MongoClients.create(System.getenv("MONGODB"));
        MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        this.db = client.getDatabase("beccacino");
    }

    public void insertDocument(Document document, String collectionName) {
        db.getCollection(collectionName).insertOne(document);
    }

    public ArrayList<Document> retrieveAllDocuments(String collectionName) {
        Iterable<Document> documentsIterable = this.db.getCollection(collectionName).find();
        ArrayList<Document> documentsList = new ArrayList<>();
        for (Document doc: documentsIterable) {
            documentsList.add(doc);
        }
        return documentsList;
    }

    public void removeDocument(String field, String id, String collectionName) {
        this.db.getCollection(collectionName).deleteOne(Filters.eq(field, id));
    }

    public MongoDatabase getDB() {
        return this.db;
    }

    /*public Document retrieveDocumentByID(String field, String id, String collectionName) {
        return db.getCollection(collectionName).find(Filters.eq(field, id)).first();
    }

    private boolean isPresent(Filters filter, String collectionName) {
        return db.getCollection(collectionName).countDocuments(Filters.eq(filter)) == 1;
    }*/
}
