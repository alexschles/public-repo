package edu.yu.cs.com1320.project.Impl;

import edu.yu.cs.com1320.project.DocumentStore.CompressionFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;

import static org.junit.Assert.*;
//Made by Ari Hagler, re-adjusted by Alex Schlesinger
public class StackTest{

/** Tests general undo and undo for a specific URI with various puts and deletes */


    private DocumentStoreImpl testStore;
    private final String testString = "I really hope these tests pass";
    private final byte[] testByte = testString.getBytes();
    private URI uri1;
    private URI uri2;
    private URI uri3;
    private URI uri4;
    private URI uri5;

    @Before
    public void doBefore(){
        testStore = new DocumentStoreImpl();
        uri1 = URI.create("http://www.yu.edu/uri1ForTest");
        uri2 = URI.create("http://www.yu.edu/uri2ForTest");
        uri3 = URI.create("http://www.yu.edu/uri3ForTest");
        uri4 = URI.create("http://www.yu.edu/uri4ForTest");
        uri5 = URI.create("http://www.yu.edu/uri5ForTest");
    }

    @Test
    public void onePutAndUndo(){
        testStore.putDocument(new ByteArrayInputStream(testByte), uri1);
        testStore.undo();
        assertNull(testStore.getDocument(uri1));
    }

    @Test
    public void twoPutsAndUndo(){
        testStore.putDocument(new ByteArrayInputStream(testByte), uri1);
        testStore.putDocument(new ByteArrayInputStream(testByte), uri2, CompressionFormat.GZIP);
        testStore.undo();
        assertEquals(testStore.getDocument(uri1), testString);
        assertNull(testStore.getDocument(uri2));
    }

    @Test
    public void twoPutsAndUndoSpecificURI(){
        testStore.putDocument(new ByteArrayInputStream(testByte), uri1);
        testStore.putDocument(new ByteArrayInputStream(testByte), uri2, CompressionFormat.GZIP);
        testStore.undo(uri1);
        assertEquals(testStore.getDocument(uri2), testString);
        assertNull(testStore.getDocument(uri1));
    }

    @Test
    public void severalPutsAndUndos(){
        testStore.putDocument(new ByteArrayInputStream(testByte), uri1);
        testStore.putDocument(new ByteArrayInputStream(testByte), uri2, CompressionFormat.GZIP);
        testStore.putDocument(new ByteArrayInputStream(testByte), uri3, CompressionFormat.BZIP2);
        testStore.undo(uri1);
        testStore.putDocument(new ByteArrayInputStream(testByte), uri4, CompressionFormat.GZIP);
        testStore.undo();
        assertNull(testStore.getDocument(uri1));
        assertEquals(testStore.getDocument(uri2), testString);
        assertEquals(testStore.getDocument(uri3), testString);
        assertNull(testStore.getDocument(uri4));
    }

    @Test
    public void onePutAndDeleteAndUndo(){
        testStore.putDocument(new ByteArrayInputStream(testByte), uri1);
        testStore.deleteDocument(uri1);
        testStore.undo();
        assertEquals(testString, testStore.getDocument(uri1));
    }

    @Test
    public void twoPutsAndDeleteAndUndo(){
        testStore.putDocument(new ByteArrayInputStream(testByte), uri1);
        testStore.putDocument(new ByteArrayInputStream(testByte), uri2, CompressionFormat.GZIP);
        testStore.deleteDocument(uri2);
        testStore.undo();
        assertEquals(testString, testStore.getDocument(uri2));
    }

    @Test
    public void PutAndDeleteAndPutAndUndoSpecificURI(){
        testStore.putDocument(new ByteArrayInputStream(testByte), uri1);
        testStore.deleteDocument(uri1);
        testStore.putDocument(new ByteArrayInputStream(testByte), uri2, CompressionFormat.GZIP);
        testStore.undo(uri1);
        assertEquals(testString, testStore.getDocument(uri1));
    }




}