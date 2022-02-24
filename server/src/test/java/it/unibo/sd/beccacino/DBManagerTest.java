package it.unibo.sd.beccacino;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DBManagerTest {
    DBManager dbManager = new DBManager();

    @AfterEach
    void clearCollection() {
        this.dbManager.getDB().getCollection("players").drop();
    }

    @BeforeEach
    void addItems() {
        this.dbManager.insertDocument(new Document("_id", "45012"), "players");
        this.dbManager.insertDocument(new Document("_id", "333"), "players");
        this.dbManager.insertDocument(new Document("_id", "4"), "players");
    }

    @Test void testCreateDBManager() {
        assertEquals("beccacino", this.dbManager.getDB().getName());
    }

    @Test void testRemoveDocuments() {
        this.dbManager.removeDocument("_id", "333", "players");
        assertEquals(2, dbManager.getDB().getCollection("players").countDocuments());
    }

    @Test void testRetrieveAllDocuments() {
        ArrayList<Document> playersList = this.dbManager.retrieveAllDocuments("players");
        assertEquals(3, playersList.size());
    }

    @Test void testInsertDocuments() {
        assertEquals(3, dbManager.getDB().getCollection("players").countDocuments());
    }

    @Test void testRetrieveDocument() {
        Document testDocument = new Document("_id", "4");
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "4", "players"));
    }

    @Test void testUpdateDocument() {
        this.dbManager.insertDocument(new Document("_id", "23").append("Nickname", "Pippo"), "players");
        Document testDocument = new Document("_id", "23").append("Nickname", "Pluto");
        assertTrue(this.dbManager.updateDocument("23", testDocument, "players").wasAcknowledged());
    }
}