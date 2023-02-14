package it.unibo.sd.beccacino;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Updates.*;

public class DBManager {
    private static final String LOBBIES_COLLECTION = "lobbies";
    private final MongoDatabase db;

    public DBManager() {
        // Use this when running server using docker.
        MongoClient client = MongoClients.create(System.getenv("MONGODB"));
        // Use this when running server locally.
        //MongoClient client = MongoClients.create("mongodb://localhost:27017");
        // Replace the uri string with your MongoDB deployment's connection string
        /*String mongoUri = "mongodb://localhost:27017";
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .build();
        MongoClient client = MongoClients.create(settings);
        */
        this.db = client.getDatabase("beccacino");
    }

    public BsonValue insertDocument(Document document, String collectionName) {
        return db.getCollection(collectionName).insertOne(document).getInsertedId();
    }

    public List<Document> retrieveAllDocuments(String collectionName) {
        Iterable<Document> documentsIterable = this.db.getCollection(collectionName).find();
        List<Document> documentsList = new ArrayList<>();
        for (Document doc : documentsIterable) {
            documentsList.add(doc);
        }
        return documentsList;
    }

    public boolean removeDocument(String field, String id, String collectionName) {
        return this.db.getCollection(collectionName).deleteOne(eq(field, new ObjectId(id))).wasAcknowledged();
    }

    public MongoDatabase getDB() {
        return this.db;
    }

    public Document retrieveDocumentByID(String field, String id, String collectionName) {
        return db.getCollection(collectionName).find(eq(field, String.valueOf(id))).first();
    }

    public UpdateResult updateDocument(String id, Document document, String collectionName) {
        return this.db.getCollection(collectionName).replaceOne(eq("_id", String.valueOf(id)), document);
    }

    public Game getGameById(String id) {
        Document gameDocument = db.getCollection("games")
                .find(eq("_id", new ObjectId(id)))
                .first();
        if (gameDocument != null) {
            ObjectId gameID = (ObjectId) gameDocument.get("_id");
            gameDocument.remove("_id");
            String gameJson = gameDocument.toJson();
            Game.Builder gameBuilder = Game.newBuilder();
            gameBuilder.setId(gameID.toString());
            try {
                JsonFormat.parser().ignoringUnknownFields().merge(gameJson, gameBuilder);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            return gameBuilder.build();
        }
        return null;
    }

    public Game getGameByLobbyId(String id) {
        Document gameDocument = db.getCollection("games")
                .find(eq("lobbyId", new ObjectId(id)))
                .first();
        if (gameDocument != null) {
            ObjectId gameID = (ObjectId) gameDocument.get("_id");
            gameDocument.remove("_id");
            String gameJson = gameDocument.toJson();
            Game.Builder gameBuilder = Game.newBuilder();
            gameBuilder.setId(gameID.toString());
            try {
                JsonFormat.parser().ignoringUnknownFields().merge(gameJson, gameBuilder);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            return gameBuilder.build();
        }
        return null;
    }

    public Lobby getLobbyById(String id) {
        Document lobbyDocument = db.getCollection(LOBBIES_COLLECTION)
                .find(eq("_id", new ObjectId(id)))
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
        Bson lobbyFilter = eq("_id", new ObjectId(lobbyId));
        Bson removedPlayer = Updates.pull("players", new Document("_id", player.getId()));
        return this.db.getCollection(LOBBIES_COLLECTION)
                .updateOne(lobbyFilter, removedPlayer)
                .wasAcknowledged();
    }

    public boolean updateLobbyPlayers(Player playerJoined, String joinLobbyId) {
        Bson lobbyFilter = eq("_id", new ObjectId(joinLobbyId));
        Bson updatedPlayer = Updates.push("players", new Document("_id", playerJoined.getId())
                .append("nickname", playerJoined.getNickname()));
        return this.db.getCollection(LOBBIES_COLLECTION)
                .updateOne(lobbyFilter, updatedPlayer).wasAcknowledged();
    }

    public boolean updateBriscola(Suit briscola, String gameID) {
        Bson filter = eq("_id", new ObjectId(gameID));
        Bson update = Updates.set("publicData.briscola", briscola);
        return this.db.getCollection("games")
                .updateOne(filter, update)
                .wasAcknowledged();
    }

    public boolean registerPlay(Card cardPlayed, String gameId) {
        Bson filter = eq("_id", new ObjectId(gameId));
        Bson update = Updates.push("publicData.cards_on_table", new Document("value", cardPlayed.getValue())
                .append("suit", cardPlayed.getSuit()));
        return this.db.getCollection("games")
                .updateOne(filter, update)
                .wasAcknowledged();
    }

    public boolean setMessage(String cardMessage, String gameId) {
        Bson filter = eq("_id", new ObjectId(gameId));
        Bson update = Updates.set("publicData.message", cardMessage);
        return this.db.getCollection("games")
                .updateOne(filter, update)
                .wasAcknowledged();
    }

    public void setDominantSuit(Suit suit, String gameId) {
        Bson filter = eq("_id", new ObjectId(gameId));
        Bson update = Updates.set("publicData.dominantSuit", suit);
        this.db.getCollection("games")
                .updateOne(filter, update).wasAcknowledged();
    }

    public void setPlayerTurn(Player player, String gameID) {
        Bson filter = eq("_id", new ObjectId(gameID));
        Bson update = Updates.set("publicData.currentPlayer", new Document("_id", player.getId())
                .append("nickname", player.getNickname()));
        this.db.getCollection("games")
                .updateOne(filter, update).wasAcknowledged();
    }
/*
    public void clearMilestones(){
        Bson filter = eq("_id", 5);
        Bson update = Updates.popFirst("milestones");

        UpdateResult result = this.db.getCollection("players").updateOne(
                Filters.eq("_id", "5"),
                Updates.popFirst("milestones")
        );
        System.out.println(result.wasAcknowledged() + " " + result.getMatchedCount() + " " + result.getModifiedCount());
        //this.db.getCollection("players").updateOne(eq("_id", 5), set("milestones", new ArrayList<>()));
    }
 */
    public void clearCardsOnTable(String gameId) {
        for(int i=0; i<4; i++) {
            Bson filter = eq("_id", new ObjectId(gameId));
            Bson update = Updates.popFirst("publicData.cards_on_table");
            this.db.getCollection("games")
                    .updateOne(filter, update);
        }
    }

    public void saveCardsWon(List<Card> cardsPlayed, String gameId, int i) {
        Bson filter = eq("_id", new ObjectId(gameId));
        cardsPlayed.forEach(card -> {
            Bson update = Updates.push("publicData.team" + i + "_card_won", new Document("value", card.getValue())
                    .append("suit", card.getSuit()));
            this.db.getCollection("games")
                    .updateOne(filter, update);
        });
    }

    public void updateTeamPoints(String gameID, int pointsMade, int teamNumber) {
        Game game = this.getGameById(gameID);
        int currentTeamScore =
                teamNumber == 1 ? game.getPublicData().getScoreTeam1() : game.getPublicData().getScoreTeam2();
        Bson filter = eq("_id", new ObjectId(gameID));
        Bson update = Updates.set("publicData.scoreTeam" + teamNumber, currentTeamScore + pointsMade);
        this.db.getCollection("games").updateOne(filter, update);
    }

    public void removeCardFromHand(String gameID, Card card, Player player){
        try {
            Bson filter = new BasicDBObject("_id", new ObjectId(gameID)).append("privateData", new BasicDBObject("$elemMatch", new BasicDBObject("player.nickname", player.getNickname())));
            Bson update = new BasicDBObject("$pull", new BasicDBObject("privateData.$.myCards", new Document("value", card.getValue()).append("suit", card.getSuit())));
            UpdateResult result = this.db.getCollection("games").updateOne(filter, update);
            System.out.println("risultato: " + result.getMatchedCount() + result.getModifiedCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            Bson filter = eq("_id", new ObjectId(gameID));
            Bson update = Updates.pull("privateData", new Document("value", card.getValue())
                    .append("suit", card.getSuit()));
            this.db.getCollection("games")
                    .updateOne(filter, update);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
