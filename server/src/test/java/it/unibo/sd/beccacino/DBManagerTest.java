package it.unibo.sd.beccacino;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
        ArrayList<Document> documentRetrieved = this.dbManager.retrieveAllDocuments(TEST_COLLECTION_NAME);
        assertEquals(3, documentRetrieved.size());
    }

    @Test void testRetrieveDocument() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(secondDocument, TEST_COLLECTION_NAME);
        Document testDocument = new Document("_id", "2");
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "2", TEST_COLLECTION_NAME));
    }

    @Test void testUpdateDocument() {
        this.dbManager.insertDocument(firstDocument.append("Nickname", "Pippo"), TEST_COLLECTION_NAME);
        Document testDocument = firstDocument.append("Nickname", "Pluto");
        this.dbManager.updateDocument("1", testDocument, TEST_COLLECTION_NAME);
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "1", TEST_COLLECTION_NAME));
    }
}