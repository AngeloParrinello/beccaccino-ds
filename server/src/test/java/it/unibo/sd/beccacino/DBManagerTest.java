package it.unibo.sd.beccacino;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DBManagerTest {
    private DBManager dbManager;
    private static final String TEST_COLLECTION_NAME = "players";
    private final Document firstDocument = new Document("_id", "1");
    private final Document secondDocument = new Document("_id", "2");
    private final Document thirdDocument = new Document("_id", "3");

    @BeforeEach
    void setup() {
        this.dbManager = new DBManager();
    }

    @AfterEach
    void clearCollection() {
        this.dbManager.getDB().getCollection("players").drop();
        this.dbManager.getDB().getCollection("lobbies").drop();
    }

    @Test void testCreateDBManager() {
        final String databaseName = "beccacino";
        assertEquals(databaseName, this.dbManager.getDB().getName());
    }

    @Test void testInsertDocuments() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        assertEquals(1, dbManager.getDB().getCollection(TEST_COLLECTION_NAME).countDocuments());
    }

    @Test void testRemoveDocuments() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(secondDocument, TEST_COLLECTION_NAME);
        this.dbManager.removeDocument("_id", "2", TEST_COLLECTION_NAME);
        assertEquals(1, dbManager.getDB().getCollection(TEST_COLLECTION_NAME).countDocuments());
    }

    @Test void testRetrieveAllDocuments() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(secondDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(thirdDocument, TEST_COLLECTION_NAME);
        List<Document> documentRetrieved = this.dbManager.retrieveAllDocuments(TEST_COLLECTION_NAME);
        assertEquals(3, documentRetrieved.size());
    }

    @Test void testRetrieveDocument() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(secondDocument, TEST_COLLECTION_NAME);
        Document testDocument = new Document("_id", "2");
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "2", TEST_COLLECTION_NAME));
    }

    @Test void testUpdateDocument() {
        this.dbManager.insertDocument(firstDocument.append("Nickname", "Pippo").append("Gender", "Male"), TEST_COLLECTION_NAME);
        Document testDocument = firstDocument.append("Nickname", "Pluto");
        this.dbManager.updateDocument("1", testDocument, TEST_COLLECTION_NAME);
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "1", TEST_COLLECTION_NAME));
    }

    @Test void testRemovePlayerFromLobby() {
        this.dbManager.insertDocument(new Document("_id", 92).append("players", new Document("_id", 57)
                .append("nickname", "Pippo")), "lobbies");
        assertTrue(this.dbManager.removeLobbyPlayer(Player.newBuilder().setId("57").setNickname("Pippo").build(), "92"));
    }

    @Test void testUpdateLobbyPlayers() {
        this.dbManager.insertDocument(new Document("_id", "92")
                .append("players", List.of(new Document("_id", 57)
                        .append("nickname", "Pippo"))), "lobbies");
        this.dbManager.updateLobbyPlayers(Player.newBuilder().setId("65").setNickname("Pluto").build(), "92");
        Document testDocument = new Document("_id", "92")
                .append("players", Arrays.asList(new Document("_id", 57).append("nickname", "Pippo"),
                                                new Document("_id", 65).append("nickname", "Pluto")));
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "92", "lobbies"));
    }
}