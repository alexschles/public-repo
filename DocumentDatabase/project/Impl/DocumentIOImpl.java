package edu.yu.cs.com1320.project.Impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.Document;
import edu.yu.cs.com1320.project.DocumentIO;


import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.DocumentStore;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DocumentIOImpl extends DocumentIO {

    protected File baseDir;
    public DocumentIOImpl(File baseDir)
    {
        this.baseDir = baseDir;
    }
    public DocumentIOImpl()
    {
        this.baseDir = null;
    }

    public File serialize(Document doc) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonSerializer<Document> serializer = new JsonSerializer<Document>() {
            @Override
            public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject object = new JsonObject();
                object.addProperty("docAsByteArray", Base64.getEncoder().withoutPadding().encodeToString(document.getDocument()));
                object.addProperty("Compression Format", document.getCompressionFormat().toString());
                object.addProperty("Key", document.getKey().toString());
                object.addProperty("hashCode", document.getDocumentHashCode());
                Map<String, Integer> map = document.getWordMap();
                Collection<Integer> values = map.values();
                int[] valueArray = new int[values.size()];
                int index = 0;
                for(Integer element : values) valueArray[index++] = element.intValue();
                Collection<String> keys = map.keySet();
                String[] keyArray = keys.toArray(new String[keys.size()]);
                String keyString = Arrays.toString(keyArray);
                String valueString = Arrays.toString(valueArray);
                object.addProperty("wordCountMapKeys", keyString);
                object.addProperty("wordCountMapValues", valueString);
                return object;
            }
        };

        Type docType = new TypeToken<Document>() {}.getType();
        gsonBuilder.registerTypeHierarchyAdapter(doc.getClass(), serializer);
        Gson customGson = gsonBuilder.setPrettyPrinting().create();
        String customJson = customGson.toJson(doc);

        String uriStringPreSplit = doc.getKey().toString().replace("http://", "").concat(".json");
        String[] uriStringArray = uriStringPreSplit.split("/");
        String uriString = "";
        for (int i = 0; i < uriStringArray.length; i++) {
            uriString += File.separator + uriStringArray[i];
        }
        String workingDirectory = System.getProperty("user.dir");
        String filepath = "";
        File file;
        if (baseDir == null) {
            filepath = workingDirectory +  uriString;
            file = new File(filepath);
        } else {
            filepath = baseDir.getPath()  + uriString;
            file = new File(filepath);
        }
        file.getParentFile().mkdirs();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.write(customJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public Document deserialize(URI uri) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonDeserializer<Document> deserializer = new JsonDeserializer<Document>() {
            private String zipDecompression(byte[] compressed) {
                String decompressed = "";
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
                    ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, bais);
                    ZipArchiveEntry entry = (ZipArchiveEntry) ais.getNextEntry();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy(ais, baos);
                    baos.close();
                    ais.close();
                    bais.close();
                    decompressed = baos.toString();
                    return decompressed;
                } catch (IOException | org.apache.commons.compress.archivers.ArchiveException e) {
                    e.printStackTrace();
                }
                return decompressed;
            }

            private String jarDecompression(byte[] compressed) {
                String decompressed = "";
                try {
                    InputStream bais = new ByteArrayInputStream(compressed);
                    ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.JAR, bais);
                    ZipArchiveEntry entry = (ZipArchiveEntry) ais.getNextEntry();
                    OutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while (-1 != (i = ais.read())) {
                        baos.write(i);
                    }
                    ais.close();
                    byte[] decompressedByteArray = ((ByteArrayOutputStream) baos).toByteArray();
                    decompressed = new String(decompressedByteArray);
                    return decompressed;
                } catch (IOException | org.apache.commons.compress.archivers.ArchiveException e) {
                    e.printStackTrace();
                }
                return decompressed;

            }

            private String gzipDecompression(byte[] compressed) {
                String decompressed = "";
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
                    GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bais);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i = 0;
                    while (-1 != (i = gzipIn.read())) {
                        baos.write(i);
                    }
                    gzipIn.close();
                    decompressed = baos.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return decompressed;
            }

            private String bZip2Decompression(byte[] compressed) {
                String decompressed = "";
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
                    InputStream bZip2In = new BZip2CompressorInputStream(bais);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while (-1 != (i = bZip2In.read())) {
                        baos.write(i);
                    }
                    decompressed = new String(baos.toByteArray());
                    bZip2In.close();
                    baos.close();
                    return decompressed;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return decompressed;
            }

            private String sevenZDecompression(byte[] compressed) {
                String decompressed = "";
                try {
                    SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(compressed);
                    SevenZFile sevenZFile = new SevenZFile(inMemoryByteChannel);
                    SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                    sevenZFile.read();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return decompressed;
            }

            private String decompressFinal(DocumentStore.CompressionFormat compressionFormat, byte[] byteArray) {
                String s = "";
                if (compressionFormat == DocumentStore.CompressionFormat.ZIP) {
                    s = this.zipDecompression(byteArray);
                }
                if (compressionFormat == DocumentStore.CompressionFormat.JAR) {
                   s = this.jarDecompression(byteArray);
                }
                if (compressionFormat == DocumentStore.CompressionFormat.GZIP) {
                    s = this.gzipDecompression(byteArray);
                }
                if (compressionFormat == DocumentStore.CompressionFormat.BZIP2) {
                   s = this.bZip2Decompression(byteArray);
                }
                if (compressionFormat == DocumentStore.CompressionFormat.SEVENZ) {
                   s = this.sevenZDecompression(byteArray);
                }
                return s;
            }
            @Override
            public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

                byte[] byteArray = Base64.getDecoder().decode(jsonElement.getAsJsonObject().get("docAsByteArray").getAsString());
                DocumentStore.CompressionFormat compressionFormat = DocumentStore.CompressionFormat.valueOf(jsonElement.getAsJsonObject().get("Compression Format").getAsString());
                URI uri = URI.create(jsonElement.getAsJsonObject().get("Key").getAsString());
                int hashCode = jsonElement.getAsJsonObject().get("hashCode").getAsInt();
                Type mapTyoe = new TypeToken<Map<String, Integer>>(){}.getType();
                String keyArrayString = jsonElement.getAsJsonObject().get("wordCountMapKeys").getAsString();
                String keyArrayStringMinusBrackets = keyArrayString.substring(1, keyArrayString.length() - 1);
                String[] keyArray = keyArrayStringMinusBrackets.split(", ");
                String valueArrayString = jsonElement.getAsJsonObject().get("wordCountMapValues").getAsString();
                String valueArrayStringMinusBrackets = valueArrayString.substring(1, valueArrayString.length() - 1);
                int[] valueArray = Arrays.stream(valueArrayStringMinusBrackets.split(", ")).mapToInt(Integer::parseInt).toArray();
                Map<String, Integer> wordCountMap = new HashMap<>();
                for (int i = 0; i < valueArray.length; i++) {
                    wordCountMap.put(keyArray[i], valueArray[i]);
                }
                String s = this.decompressFinal(compressionFormat, byteArray);
                DocumentImpl document = new DocumentImpl(uri, hashCode, compressionFormat, byteArray, wordCountMap, s);
                return document;

            }
        };
        File file = null;
        if (baseDir == null) {
            file = new File(System.getProperty("user.dir"), uri.toString().replace("http://", "").concat(".json"));
        } else {
            file = new File(baseDir.getAbsolutePath(), uri.toString().replace("http://", "").concat(".json"));
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fileReader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = "";
        try {
            str = FileUtils.readFileToString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gsonBuilder.registerTypeHierarchyAdapter(Document.class, deserializer);
        Gson customGson = gsonBuilder.create();
        Document customObject = customGson.fromJson(str, Document.class);

        return customObject;
    }



    public static void main(String[] args) throws URISyntaxException {


    }

}