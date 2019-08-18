package edu.yu.cs.com1320.project.Impl;
//Made by Shalom Gottesman, edited and changed by Noah Kalandar
import edu.yu.cs.com1320.project.DocumentStore;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JudahStage4TestEdited
{
	private File testingSubDirectory;
	
	private int setBaseDir() {
		Random rand = new Random();
		int x  = rand.nextInt();
		File dir = new File(System.getProperty("user.dir") + "/testing/" + x + "/"); 
		testingSubDirectory = dir;
		return x;
	}
	
	private void cleanUpFolder() {
		try {
			FileUtils.deleteDirectory(testingSubDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}//this will delete all the directory the test creates after the test is completed
	}

    @Test 
    public void testMaxDocsPre() throws Exception
    {
    	int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        //this should result doc #1 and #2 being pushed out of memory when we add 3 and 4
        dsi.setMaxDocumentCount(2);
        sleep();

        //create and add doc1
        String str1 = "this is doc#1";
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    }

    @Test
    public void testMaxDocsPost() throws Exception
    {
    	int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        sleep();
        //create and add doc1
        String str1 = "this is doc#1";
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        //this should push doc #1 and #2 out of memory, but 3 and 4 should remain
        dsi.setMaxDocumentCount(2);
        sleep();
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    }

    @Test
    public void testMaxBytesPre() throws Exception
    {
    	int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        dsi.setDefaultCompressionFormat(DocumentStore.CompressionFormat.GZIP);
        sleep();
        //create and add doc1
        String str1 = "this is doc#1";
        int compressedLength = JDCompressionUtility.compressAsGzip(str1).length;
        //this should result doc #1 and #2 being pushed out of memory when we add 3 and 4
        dsi.setMaxDocumentBytes( compressedLength * 2);
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    }

    @Test
    public void testMaxBytesPost() throws Exception
    {
    	int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        dsi.setDefaultCompressionFormat(DocumentStore.CompressionFormat.GZIP);
        sleep();
        //create and add doc1
        String str1 = "this is doc#1";
        int compressedLength = JDCompressionUtility.compressAsGzip(str1).length;
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        //this should push doc #1 and #2 out of memory, but 3 and 4 should remain
        dsi.setMaxDocumentBytes( compressedLength * 2);
        sleep();
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    }

    @Test
    public void testBothMaxDocsAndBytesPre() throws Exception
    {
    	int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        dsi.setDefaultCompressionFormat(DocumentStore.CompressionFormat.GZIP);
        sleep();
        //create and add doc1
        String str1 = "this is doc#1";
        int compressedLength = JDCompressionUtility.compressAsGzip(str1).length;
        dsi.setMaxDocumentBytes( compressedLength * 2);
        //this should result in all but doc #4 being out of memory
        dsi.setMaxDocumentCount(1);
        sleep();
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    }


    @Test
    public void testBothMaxDocsAndBytesPost() throws Exception
    {
        int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        dsi.setDefaultCompressionFormat(DocumentStore.CompressionFormat.GZIP);
        sleep();
        //create and add doc1
        String str1 = "this is doc#1";
        int compressedLength = JDCompressionUtility.compressAsGzip(str1).length;
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        //this should result in all but doc #4 being out of memory
        dsi.setMaxDocumentBytes(compressedLength * 2);
        dsi.setMaxDocumentCount(1);
        sleep();
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    }
    

    @Test
    public void testMaxDocsWithOneReheapify() throws Exception
    {
        int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        sleep();
        //int compressedLength;
        //create and add doc1
        String str1 = "this is doc#1";
        //compressedLength = JDCompressionUtility.compressAsGzip(str1).length;
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        //reset doc1 to be most recent
        dsi.getDocument(uri1);
        //this should push doc #2 out of memory, others should remain
        dsi.setMaxDocumentCount(3);
        sleep();
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    }
    
    @Test
    public void testMaxDocsWithTwpReheapifies() throws Exception
    {
        int x = setBaseDir();
        DocumentStoreImpl dsi = new DocumentStoreImpl(testingSubDirectory);
        sleep();
        //int compressedLength;
        //create and add doc1
        String str1 = "this is doc#1";
        //compressedLength = JDCompressionUtility.compressAsGzip(str1).length;
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis,uri1);
        sleep();
        //create and add doc2
        String str2 = "this is doc#2";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(str2.getBytes());
        dsi.putDocument(bis,uri2);
        sleep();
        //create and add doc3
        String str3 = "this is doc#3";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(str3.getBytes());
        dsi.putDocument(bis,uri3);
        sleep();
        //create and add doc4
        String str4 = "this is doc#4";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(str4.getBytes());
        dsi.putDocument(bis,uri4);
        sleep();
        //reset doc1 to be most recent
        dsi.getDocument(uri1);
        dsi.getDocument(uri2);
        //this should push doc #3 and doc #4 out of memory, 1 and 2 should remain
        dsi.setMaxDocumentCount(2);
        sleep();
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc2.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc3.json").exists());
        assertTrue (new File(System.getProperty("user.dir") + "/testing/" +x + "/www.yu.edu/doc4.json").exists());
        cleanUpFolder();
    } 
    
    
    private void sleep() {
    	try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}