package edu.yu.cs.com1320.project.test.stage5;

import edu.yu.cs.com1320.project.Impl.DocumentStoreImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;
//Made by Professor Diament
public class OfficialStage3Test
{
    @Test
    public void testResultCountAndOrder() throws Exception
    {
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        //create and add doc1
        String doc1 = "this is a document";
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(doc1.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri1);

        //create and add doc2
        String doc2 = "this is a document this is a document";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(doc2.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri2);

        //create and add doc3
        String doc3 = "this is a document this is a document this is a document";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(doc3.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri3);

        //create and add doc4
        String doc4 = "this is a document this is a document this is a document this is a document";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(doc4.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri4);

        //just making sure get/put still works....
        Assert.assertEquals("string documents were not equal for doc1", doc1, dsi.getDocument(uri1));
        Assert.assertEquals("string documents were not equal for doc4", doc4, dsi.getDocument(uri4));

        //does search return the correct number of results?
        List<String> results = dsi.search("document");
        Assert.assertEquals("wrong number of results returned",4,results.size());
        

        //does search return the results in the correct order, i.e. descending?
        Assert.assertEquals("doc4 is not the first result: doc4 is \"" + doc4 + "\", but the first result is \"" + results.get(0),doc4,results.get(0));
        Assert.assertEquals("doc3 is not the second result: doc3 is \"" + doc3 + "\", but the second result is \"" + results.get(1),doc3,results.get(1));
        Assert.assertEquals("doc2 is not the third result: doc2 is \"" + doc2 + "\", but the third result is \"" + results.get(2),doc2,results.get(2));
        Assert.assertEquals("doc1 is not the fourth result: doc1 is \"" + doc1 + "\", but the fourth result is \"" + results.get(3),doc1,results.get(3));
    }

    /**
     * same as testResultCountAndOrder, but the word "document" has been given capital letters in various places
     * @throws Exception
     */
    @Test
    public void testCaseInsensitivity() throws Exception
    {
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        //create and add doc1
        String doc1 = "this is a doCument";
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(doc1.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri1);

        //create and add doc2
        String doc2 = "this is a docUment this is a documeNt";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(doc2.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri2);

        //create and add doc3
        String doc3 = "this is a document this is a documEnt this is a documenT";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(doc3.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri3);

        //create and add doc4
        String doc4 = "this is a Document this is a dOcument this is a document this is a doCument";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(doc4.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri4);

        //does search return the correct number of results?
        List<String> results = dsi.search("document");
        Assert.assertEquals("wrong number of results returned",4,results.size());

        //does search return the results in the correct order, i.e. descending?
        Assert.assertEquals("doc4 is not the first result: doc4 is \"" + doc4 + "\", but the first result is \"" + results.get(0),doc4,results.get(0));
        Assert.assertEquals("doc3 is not the second result: doc3 is \"" + doc3 + "\", but the second result is \"" + results.get(1),doc3,results.get(1));
        Assert.assertEquals("doc2 is not the third result: doc2 is \"" + doc2 + "\", but the third result is \"" + results.get(2),doc2,results.get(2));
        Assert.assertEquals("doc1 is not the fourth result: doc1 is \"" + doc1 + "\", but the fourth result is \"" + results.get(3),doc1,results.get(3));
    }

    @Test
    public void testUndo() throws Exception
    {
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        //create and add doc1
        String doc1 = "this is a document";
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(doc1.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri1);

        //should remove doc1
        dsi.undo();

        //create and add doc2
        String doc2 = "this is a document this is a document";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(doc2.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri2);

        //create and add doc3
        String doc3 = "this is a document this is a document this is a document";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(doc3.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri3);

        //create and add doc4
        String doc4 = "this is a document this is a document this is a document this is a document";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(doc4.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri4);

        //should delete doc3
        dsi.undo(uri3);

        //does search return the correct number of results? should return doc4 and doc2
        List<String> results = dsi.search("document");
        Assert.assertEquals("wrong number of results returned",2,results.size());

        //does search return the results in the correct order, i.e. descending?
        Assert.assertEquals("doc4 is not the first result: doc4 is \"" + doc4 + "\", but the first result is \"" + results.get(0),doc4,results.get(0));
        Assert.assertEquals("doc2 is not the second result: doc2 is \"" + doc2 + "\", but the second result is \"" + results.get(1),doc2,results.get(1));
    }

    @Test
    public void testDelete() throws Exception
    {
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        //create and add doc1
        String doc1 = "this is a document";
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(doc1.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri1);

        //create and add doc2
        String doc2 = "this is a document this is a document";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(doc2.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri2);

        //create and add doc3
        String doc3 = "this is a document this is a document this is a document";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(doc3.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri3);

        //create and add doc4
        String doc4 = "this is a Document. this is a document this is a document this is a document";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(doc4.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri4);

        //should remove doc2 and doc 4
        dsi.deleteDocument(uri2);
        dsi.deleteDocument(uri4);
        
        //does search return the correct number of results? should return doc3 and doc1
        List<String> results = dsi.search("document");
        Assert.assertEquals("wrong number of results returned",2,results.size());

        //does search return the results in the correct order, i.e. descending?
        Assert.assertEquals("doc3 is not the first result: doc3 is \"" + doc3 + "\", but the first result is \"" + results.get(0),doc3,results.get(0));
        Assert.assertEquals("doc1 is not the second result: doc1 is \"" + doc1 + "\", but the second result is \"" + results.get(1),doc1,results.get(1));
    }

    @Test
    public void testUndoAndDelete() throws Exception
    {
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        //create and add doc1
        String doc1 = "this is a document";
        URI uri1 = new URI("http://www.yu.edu/doc1");
        ByteArrayInputStream bis = new ByteArrayInputStream(doc1.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri1);
        
        //should remove doc1
        dsi.undo();

        //create and add doc2
        String doc2 = "this is a document this is a document";
        URI uri2 = new URI("http://www.yu.edu/doc2");
        bis = new ByteArrayInputStream(doc2.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri2);

        //create and add doc3
        String doc3 = "this is a document this is a document this is a document";
        URI uri3 = new URI("http://www.yu.edu/doc3");
        bis = new ByteArrayInputStream(doc3.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri3);
        
        //should remove doc2
        dsi.deleteDocument(uri2);
        
        //create and add doc4
        String doc4 = "this is a document this is a document this is a document this is a document";
        URI uri4 = new URI("http://www.yu.edu/doc4");
        bis = new ByteArrayInputStream(doc4.getBytes());
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri4);
        
        //create and add doc5
        String doc5 = "this is a document this is a document this is a document this is a document this is a document";
        URI uri5 = new URI("http://www.yu.edu/doc5");
        bis = new ByteArrayInputStream(doc5.getBytes()); //this line used to have bis = new ByteArrayInputStream(doc4.getBytes()) 
        //put, and then get to confirm storage works
        dsi.putDocument(bis, uri5);

        //should delete doc4
        dsi.undo(uri4);

        //does search return the correct number of results? should return doc5 and doc3
        List<String> results = dsi.search("document");
        Assert.assertEquals("wrong number of results returned",2,results.size());
        

        //does search return the results in the correct order, i.e. descending?
        Assert.assertEquals("doc5 is not the first result: doc5 is \"" + doc5 + "\", but the first result is \"" + results.get(0),doc5,results.get(0));
        Assert.assertEquals("doc3 is not the second result: doc3 is \"" + doc3 + "\", but the second result is \"" + results.get(1),doc3,results.get(1));
    }
}