package edu.yu.cs.com1320.project.Impl;
import edu.yu.cs.com1320.project.Document;
import edu.yu.cs.com1320.project.DocumentIO;
import edu.yu.cs.com1320.project.DocumentStore;
import edu.yu.cs.com1320.project.Impl.DocumentIOImpl;
import edu.yu.cs.com1320.project.Impl.DocumentImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
//Made by Ari Roffe, re-adjusted by Alex Schlesinger
public class DocumentIOImplTest
{
   private File baseDir;
   private DocumentIO documentIO;
   private Document document;
   private URI one;

   public DocumentIOImplTest(){
      try{
         this.one = new URI("https://www.yu.edu/documents/doc43");
      }catch(URISyntaxException u){
         u.printStackTrace();
      }
      baseDir = new File(System.getProperty("user.dir"));
      documentIO = new DocumentIOImpl(baseDir);
      String s = "hi";
      String textOne = "Well this is the finish line";
      document = new DocumentImpl(this.one, Math.abs(textOne.hashCode()),  DocumentStore.CompressionFormat.JAR, textOne.getBytes(), this.makeMapping(textOne), s);
   }

   /**
    * This test demonstrates the logic I used to take a URI and break it down into a classpath.
    * Once broken down into a classpath, I appended the directories to the baseDir
    * After this, I create a file on disc (provided that file on disc does not exist)
    * Last I write the contents to disc using a FileWriter
    */
   @Test
   public void createFile(){

      System.out.println(baseDir.getAbsolutePath());
      try
      {
         URI uri = new URI("http://www.yu.edu/documents/doc390");//given the complete uri
         String path = uri.getAuthority()  + this.disectURI(uri);//get the Path which is www.yu.edu\documents
         File file = new File(baseDir.getAbsolutePath() + File.separator + path);//make a File with the full class path and with the "URI Path"
         System.out.println(file.getAbsolutePath());// "C:\Users\Ari\GitHub\RoffeAri\DataStructures\project\stage5\www.yu.edu\documents"
         Files.createDirectories(file.toPath());//create the directories we didn't have before, ie not apart of the baseDir

         File theFile = new File(file.getAbsolutePath() + File.separator + "doc390.txt");//create a new File with the document as the last piece
         System.out.println(theFile.getAbsolutePath());// "C:\Users\Ari\GitHub\RoffeAri\DataStructures\project\stage5\www.yu.edu\documents\doc2.txt"
         //wrap in a try because if the file is already there than we don't need to create a file
         if(!theFile.exists())Files.createFile(theFile.toPath());//create this file in the computer

         //write to the file -- in the real application this is the writing of the json
         FileWriter fileWriter = new FileWriter(theFile);
         fileWriter.write("HaYom sishi BShabbos");
         fileWriter.flush();
         fileWriter.close();

      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   /**
    * This test confirms that my serialize and deserialize methods for DocumentIOImpl work
    */
   @Test
   public void test2()
   {
      documentIO.serialize(document);
      documentIO.deserialize(document.getKey());
   }

   private String disectURI(URI uri)
   {
      String path = uri.getPath();

      String[] splitter = path.split("/");
      StringBuilder stringBuilder = new StringBuilder();
      for(int i = 0; i < splitter.length - 1; i++)
      {
         stringBuilder.append(splitter[i]).append(File.separator);
      }
      return stringBuilder.toString();

   }

   private Map<String, Integer> makeMapping(String string){
      HashMap<String, Integer> result = new HashMap<>();
      String[] arr = string.split("\\s");
      for(String s : arr)
      {
         result.merge(s, 1, (a, b) -> a + b);

      }
      return result;
   }
}