
package edu.yu.cs.com1320.project.Impl;
import edu.yu.cs.com1320.project.Impl.DocumentStoreImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;

import static org.junit.Assert.*;
//made by Ari Hagler, adjusted and added onto by Alex Schlesinger
public class ComprehensiveTest {

    /** Tests serialization and deserialization when utilizing combinations of put, delete, MaxDocs, MaxBytes, search, and undo. */

    private DocumentStoreImpl test;
    private final String s1 = "This is the first (1st) test and I hope it's a very good test!"; // zipCompressed.length = 180
    private final String s2 = s1 + " " + s1; // zipCompressed.length = 184
    private final String s3 = s1 + " it's its {3rd] test is also good?"; // zipCompressed.length = 202
    private final String s4 = "last test : #great"; // zipCompressed.length = 140
    private final byte[] b1 = s1.getBytes();
    private final byte[] b2 = s2.getBytes();
    private final byte[] b3 = s3.getBytes();
    private final byte[] b4 = s4.getBytes();
    private URI uri1 = URI.create("http://www.yu.edu/doc1");
    private URI uri2 = URI.create("http://www.yu.edu/doc2");
    private URI uri3 = URI.create("http://www.yu.edu/doc3");
    private URI uri4 = URI.create("http://www.yu.edu/doc4");

    @Before
    public void doBefore(){
        test = new DocumentStoreImpl();
    }

    @Test (expected = IllegalArgumentException.class)
    public void maxByte0(){
        test.setMaxDocumentBytes(0);
        test.putDocument(new ByteArrayInputStream(b1), uri1);
    }
/** isOnDisk is a protected method in my Document Store that returns a boolean determining
 *  whether or not the given URI is in a certain Arraylist of URIs in my Document Store.
 *  A URI is only added to that ArrayList when the Document associated with it has gone through the serialization process. */
    @Test
    public void remove1MaxDoc(){
        test.setMaxDocumentCount(1);
        test.putDocument(new ByteArrayInputStream(b1), uri1);
        test.putDocument(new ByteArrayInputStream(b2), uri2);
        assertTrue(test.isOnDisk(uri1));
        assertEquals("uri1 should have deserialized", "This is the first (1st) test and I hope it's a very good test!", test.getDocument(uri1) );

    }


    @Test
    public void remove1MaxByte(){
        test.setMaxDocumentBytes(203);
        test.putDocument(new ByteArrayInputStream(b3), uri3);
        test.putDocument(new ByteArrayInputStream(b4), uri4);
        assertTrue(test.isOnDisk(uri3));
        assertEquals("uri1 should have deserialized","This is the first (1st) test and I hope it's a very good test! it's its {3rd] test is also good?" , test.getDocument(uri3) );
    }

    @Test
    public void getChangesTime(){
        test.setMaxDocumentCount(3);
        test.putDocument(new ByteArrayInputStream(b1), uri1);
        sleep();
        test.putDocument(new ByteArrayInputStream(b2), uri2);
        sleep();
        test.putDocument(new ByteArrayInputStream(b3), uri3);
        // delay so it won't record putting b3 and getting b1 as being at the same time
        sleep();
        test.getCompressedDocument(uri1);
        sleep();
        test.getDocument(uri2);
        sleep();
        test.putDocument(new ByteArrayInputStream(b4), uri4);
        assertTrue(test.isOnDisk(uri3));
    }

    @Test
    public void searchChangesTime(){
        test.setMaxDocumentCount(3);
        test.putDocument(new ByteArrayInputStream(b1), uri1);
        sleep();
        test.putDocument(new ByteArrayInputStream(b2), uri2);
        sleep();
        test.putDocument(new ByteArrayInputStream(b4), uri4);
        sleep();
        test.search("i"); // b4 doesn't have any "i" so will be last used
        sleep();
        test.putDocument(new ByteArrayInputStream(b3), uri3);
        assertTrue(test.isOnDisk(uri4));
    }

    @Test
    public void searchCompressedChangesTime(){
        test.setMaxDocumentCount(3);
        test.putDocument(new ByteArrayInputStream(b1), uri1);
        sleep();
        test.putDocument(new ByteArrayInputStream(b2), uri2);
        sleep();
        test.putDocument(new ByteArrayInputStream(b4), uri4);
        sleep();
        test.searchCompressed("i"); // b4 doesn't have any "i" so will be last used
        sleep();
        test.putDocument(new ByteArrayInputStream(b3), uri3);
        assertTrue(test.isOnDisk(uri4));
    }

    @Test
    public void undoPushingPastLimit(){
        test.setMaxDocumentCount(3);
        test.putDocument(new ByteArrayInputStream(b1), uri1);
        sleep();
        test.putDocument(new ByteArrayInputStream(b2), uri2);
        sleep();
        test.deleteDocument(uri2);
        sleep();
        test.putDocument(new ByteArrayInputStream(b3), uri3);
        sleep();
        test.putDocument(new ByteArrayInputStream(b4), uri4);
        sleep();
        test.undo(uri2);
        assertTrue(test.isOnDisk(uri1));
    }


    @Test
    public void undoPushing2PastLimit(){
        test.setMaxDocumentBytes(575);
        test.putDocument(new ByteArrayInputStream(b1), uri1);
        sleep();
        test.putDocument(new ByteArrayInputStream(b2), uri2);
        sleep();
        test.deleteDocument(uri2);
        sleep();
        test.putDocument(new ByteArrayInputStream(b3), uri3);
        sleep();
        test.putDocument(new ByteArrayInputStream(b4), uri4);
        sleep();
        test.undo(uri2);
        assertTrue(test.isOnDisk(uri1));
    }

    @Test
    public void undoAddingTooLarge(){
        test.putDocument(new ByteArrayInputStream(b1), uri1);
        sleep();
        test.putDocument(new ByteArrayInputStream(b3), uri3);
        sleep();
        test.deleteDocument(uri3);
        sleep();
        test.setMaxDocumentBytes(205);
        sleep();
        test.undo();
        assertTrue(test.isOnDisk(uri1));
    }

    private void sleep(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}