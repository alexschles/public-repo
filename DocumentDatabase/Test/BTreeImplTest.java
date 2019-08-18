package edu.yu.cs.com1320.project.Impl;
import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.Document;
import edu.yu.cs.com1320.project.DocumentStore;
import edu.yu.cs.com1320.project.Impl.BTreeImpl;
import edu.yu.cs.com1320.project.Impl.DocumentImpl;
import edu.yu.cs.com1320.project.Impl.DocumentStoreImpl;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
//Made by Ari Roffe, re-adjusted by Alex Schlesinger
public class BTreeImplTest
{
   private BTree<URI, Document> bTree;
   private File file;
   private String path;

   private URI one, two, three;
   private Document doc1, doc2, doc3;
   private String message1, message2, message3;
   private byte[] byte1, byte2, byte3;

   private DocumentStore documentStore;

   public BTreeImplTest()
   {
      path = System.getProperty("user.dir");
      file = new File(path);
      bTree = new BTreeImpl(file);
      documentStore = new DocumentStoreImpl();

      try
      {
         this.one = new URI("https://www.yu.edu/doc1");
         this.two = new URI("https://www.yu.edu/doc2");
         this.three = new URI("https://www.yu.edu/doc3");
      } catch(URISyntaxException u)
      {
         u.printStackTrace();
      }

      message1 = "Summer break started but we still have stage five";
      message2 = "After friday afternoon we will not have any data structures work left";
      message3 = "Also after friday afternoon also comes shabbos hakadosh";

      byte1 = message1.getBytes();
      byte2 = message2.getBytes();
      byte3 = message3.getBytes();
      String s = "hi";

      doc1 = new DocumentImpl(one, Math.abs(message1.hashCode()),  DocumentStore.CompressionFormat.SEVENZ, byte1, this.makeMapping(message1),s );
      doc2 = new DocumentImpl(two, Math.abs(message2.hashCode()), DocumentStore.CompressionFormat.ZIP, byte2, this.makeMapping(message2), s);
      doc3 = new DocumentImpl(three, Math.abs(message3.hashCode()), DocumentStore.CompressionFormat.JAR, byte3, this.makeMapping(message3), s);


   }

   @Test
   public void testPutMethod()
   {
      bTree.put(one, doc1);
      bTree.put(two, doc2);
      bTree.put(three, doc3);
   }

   @Test
   public void testingGetMethod()
   {
      bTree.get(one);
      bTree.get(two);
      bTree.get(three);
   }

   private Map<String, Integer> makeMapping(String string) {
      HashMap<String, Integer> result = new HashMap<>();
      String[] arr = string.split("\\s");
      for(int i = 0; i < arr.length; i++) {
         if(result.get(arr[i]) != null) {
            result.put(arr[i], 1);
         }else{
            result.put(arr[i], result.get(arr[i]));
         }

      }
      return result;
   }

   @Test
   public void introducingDocumentStoreWithBTree()
   {
      documentStore.putDocument(new ByteArrayInputStream(byte1), one);
      assertNotNull(documentStore.getDocument(one));
   }
}