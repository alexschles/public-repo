package edu.yu.cs.com1320.project.Impl;
import edu.yu.cs.com1320.project.Document;
import edu.yu.cs.com1320.project.DocumentStore;

import edu.yu.cs.com1320.project.Impl.DocumentImpl;
import edu.yu.cs.com1320.project.Impl.DocumentStoreImpl;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
//Made by Ari Roffe, re-adjusted by Alex Schlesinger
public class DocumentStoreImplTest
{
   private final String string5;
   private final ByteArrayInputStream stream5;
   private URI fifthID;
   private Document doc1, doc2, doc3, doc4;
   private DocumentStore documentStore;
   private URI firstID, secondID, thirdID, fourthID;
   private String string1, string2, string3, string4;
   private InputStream stream1, stream2, stream3, stream4;
   private int byteTotal;

   public DocumentStoreImplTest() throws InterruptedException
   {
      byteTotal = 0;
      documentStore = new DocumentStoreImpl();
      try
      {
         firstID = new URI("https://www.facebook.com/documents/doc1");
         secondID = new URI("https://www.twitter.com/documents/doc2");
         thirdID = new URI("https://www.yu.edu/documents/doc3");
         fourthID = new URI("https://www.google.com/documents/doc4");

         fifthID = new URI("www.amazon.com/");
      } catch(URISyntaxException e)
      {
         e.printStackTrace();
      }
      string1 = "Hello my name hell is Bart Simpson";

      string2 = "hello my name is bart simpson. hello world!";
      string3 = "Welcome to Punctu'ation Lan4d, hell whe]re you'll find many ty9o,s";
      string4 = "CaMeL CaSe, is he, isn't bart n33d3d hel for bart class's";

      string5 = "this is the only new string for stage four. It is here for to stay. perhaps for stage five!";

      stream1 = new ByteArrayInputStream(string1.getBytes());
      stream2 = new ByteArrayInputStream(string2.getBytes());
      stream3 = new ByteArrayInputStream(string3.getBytes());
      stream4 = new ByteArrayInputStream(string4.getBytes());

      stream5 = new ByteArrayInputStream(string5.getBytes());

      documentStore.setDefaultCompressionFormat(DocumentStore.CompressionFormat.ZIP);
      documentStore.putDocument(stream1, firstID);
      Thread.sleep(50);
      documentStore.putDocument(stream2, secondID, DocumentStore.CompressionFormat.GZIP);
      Thread.sleep(50);
      documentStore.putDocument(stream3, thirdID, DocumentStore.CompressionFormat.BZIP2);
      Thread.sleep(50);
      documentStore.putDocument(stream4, fourthID, DocumentStore.CompressionFormat.SEVENZ);
      Thread.sleep(50);

      Map<String, Integer> map = new HashMap<>();
      Map<String, Integer> map2 = new HashMap<>();
      int i = 1;
      for(String element : string1.split("//s"))
      {
         map.put(element, i);
         map2.put(element, i);
         i++;
      }
      String s = "hi";
      doc1 = new DocumentImpl(secondID, 23456, DocumentStore.CompressionFormat.BZIP2, string1.getBytes(), map, s);
      doc2 = new DocumentImpl(thirdID, 23322,  DocumentStore.CompressionFormat.GZIP, string2.getBytes() , map2, s);
   }

   @Test
   public void test3() throws InterruptedException
   {
      documentStore.setMaxDocumentCount(4);
      Thread.sleep(50);
      documentStore.putDocument(stream5, fifthID);
      Thread.sleep(50);

      int a = documentStore.getCompressedDocument(secondID).length;
      int b = documentStore.getCompressedDocument(thirdID).length;
      //int c = documentStore.getCompressedDocument(fourthID).length;
      int d = documentStore.getCompressedDocument(fifthID).length;

      byteTotal = a + b + d;
      documentStore.setMaxDocumentCount(10);
      documentStore.setMaxDocumentBytes(byteTotal);

      documentStore.putDocument(stream1, firstID);
   }
}
