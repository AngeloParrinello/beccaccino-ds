package it.unibo.sd.beccacino;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DBManagerTest {
    private static final String TEST_COLLECTION_NAME = "players";
    private final DBManager dbManager = new DBManager();
    private final Document firstDocument = new Document("_id", "1");
    private final Document secondDocument = new Document("_id", "2");
    private final Document thirdDocument = new Document("_id", "3");

    @AfterEach
    void clearCollection() {
        this.dbManager.getDB().getCollection("players").drop();
        this.dbManager.getDB().getCollection("lobbies").drop();
    }

    /**
     * Test if the system correctly create the database.
     */
    @Test
    void testCreateDBManager() {
        final String databaseName = "beccacino";
        assertEquals(databaseName, this.dbManager.getDB().getName());
    }

    /**
     * Test if the system correctly insert a document in the database.
     */
    @Test
    void testInsertDocuments() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        assertEquals(1, dbManager.getDB().getCollection(TEST_COLLECTION_NAME).countDocuments());
    }

    /**
     * Test if the system correctly retrieve all the documents from a specific collection.
     */
    @Test
    void testRetrieveAllDocuments() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(secondDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(thirdDocument, TEST_COLLECTION_NAME);
        List<Document> documentRetrieved = this.dbManager.retrieveAllDocuments(TEST_COLLECTION_NAME);
        assertEquals(3, documentRetrieved.size());
    }

    /**
     * Test if the system correctly retrieve one document from a specific collection.
     */
    @Test
    void testRetrieveDocument() {
        this.dbManager.insertDocument(firstDocument, TEST_COLLECTION_NAME);
        this.dbManager.insertDocument(secondDocument, TEST_COLLECTION_NAME);
        Document testDocument = new Document("_id", "2");
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "2", TEST_COLLECTION_NAME));
    }

    /**
     * Test if the system correctly update a document from a specific collection.
     */
    @Test
    void testUpdateDocument() {
        this.dbManager.insertDocument(firstDocument.append("Nickname", "Pippo").append("Gender", "Male"), TEST_COLLECTION_NAME);
        Document testDocument = firstDocument.append("Nickname", "Pluto");
        this.dbManager.updateDocument("1", testDocument, TEST_COLLECTION_NAME);
        assertEquals(testDocument, this.dbManager.retrieveDocumentByID("_id", "1", TEST_COLLECTION_NAME));
    }
}