package it.unibo.sd.beccacino;

// imports to use when running server locally.
// imports to use when running server using docker.
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class DBManager {
    private final MongoDatabase db;
    
    public DBManager() {
        // Use this when running server using docker.
        //MongoClient client = MongoClients.create(System.getenv("MONGODB"));
        // Use this when running server locally.
        //MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        // Replace the uri string with your MongoDB deployment's connection string
        String mongoUri = "mongodb://localhost:27017";
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .build();
        MongoClient client = MongoClients.create(settings);
        this.db = client.getDatabase("beccacino");
    }

    public BsonValue insertDocument(Document document, String collectionName) {
        return db.getCollection(collectionName).insertOne(document).getInsertedId();
    }

    public ArrayList<Document> retrieveAllDocuments(String collectionName) {
        Iterable<Document> documentsIterable = this.db.getCollection(collectionName).find();
        ArrayList<Document> documentsList = new ArrayList<>();
        for (Document doc: documentsIterable) {
            documentsList.add(doc);
        }
        return documentsList;
    }

    public boolean removeDocument(String field, String id, String collectionName) {
        return this.db.getCollection(collectionName).deleteOne(Filters.eq(field, String.valueOf(id))).wasAcknowledged();
    }

    public MongoDatabase getDB() {
        return this.db;
    }

    public Document retrieveDocumentByID(String field, String id, String collectionName) {
        return db.getCollection(collectionName).find(Filters.eq(field, String.valueOf(id))).first();
    }

    public UpdateResult updateDocument(String id, Document document, String collectionName) {
        return this.db.getCollection(collectionName).replaceOne(Filters.eq("_id", String.valueOf(id)), document);
    }

    public Lobby getLobbyById(String id) {
        Document lobbyDocument = db.getCollection("lobbies")
                            .find(Filters.eq("_id",new ObjectId(id)))
                            .first();
        if (lobbyDocument != null) {
            ObjectId lobbyID = (ObjectId) lobbyDocument.get("_id");
            lobbyDocument.remove("_id");
            String lobbyJson = lobbyDocument.toJson();
            Lobby.Builder lobby = Lobby.newBuilder();
            lobby.setId(lobbyID.toString());
            try {
                JsonFormat.parser().ignoringUnknownFields().merge(lobbyJson, lobby);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            return lobby.build();
        } else {
            return null;
        }
    }

    public boolean removeLobbyPlayer(Player player, String lobbyId) {
        Bson lobbyFilter = Filters.eq("_id", String.valueOf(lobbyId));
        Bson removedPlayer = Updates.pull("players", new Document("_id", player.getId()));
        return this.db.getCollection("lobbies")
                .updateOne(lobbyFilter, removedPlayer).wasAcknowledged();
    }

    public boolean updateLobbyPlayers(Player playerJoined, String joinLobbyId) {
        Bson lobbyFilter = Filters.eq("_id", new ObjectId(joinLobbyId));
        Bson updatedPlayer = Updates.push("players", new Document("_id", playerJoined.getId())
                        .append("nickname", playerJoined.getNickname()));
        return this.db.getCollection("lobbies")
                .updateOne(lobbyFilter, updatedPlayer).wasAcknowledged();
    }
}
