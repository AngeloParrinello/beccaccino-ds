package it.unibo.sd.beccacino;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DBManagerTest {
    private DBManager dbManager;
    static final int DOCUMENT_NUMBER = 3;

    @BeforeEach
    void setup() {
        this.dbManager = new DBManager();
        this.dbManager.insertDocument(new Document("_id", "45012"), "players");
        this.dbManager.insertDocument(new Document("_id", "333"), "players");
        this.dbManager.insertDocument(new Document("_id", "4"), "players");
    }

    @AfterEach
    void clearCollection() {
        this.dbManager.getDB().getCollection("players").drop();
    }

    @Test void testCreateDBManager() {
        final String databaseName = "beccacino";
        assertEquals(databaseName, this.dbManager.getDB().getName());
    }

    @Test void testInsertDocuments() {
        assertEquals(DOCUMENT_NUMBER, dbManager.getDB().getCollection("players").countDocuments());
    }

    @Test void testRemoveDocuments() {
        this.dbManager.removeDocument("_id", "333", "players");
        assertEquals(DOCUMENT_NUMBER - 1, dbManager.getDB().getCollection("players").countDocuments());
    }

    @Test void testRetrieveAllDocuments() {
        ArrayList<Document> documentRetrieved = this.dbManager.retrieveAllDocuments("players");
        assertEquals(DOCUMENT_NUMBER, documentRetrieved.size());
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