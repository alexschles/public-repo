package edu.yu.cs.com1320.project.Impl;
import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.Document;
import edu.yu.cs.com1320.project.DocumentIO;
import edu.yu.cs.com1320.project.DocumentStore;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;



public class DocumentStoreImpl implements DocumentStore {


    private BTreeImpl<URI, DocumentImpl> bTree;
    private CompressionFormat defaultCompressionFormat;
    private StackImpl<Command> commandStack;
    private TrieImpl<URI> trie;
    private String searchWord;
    private Comparator<URI> comparator;
    private MinHeapImpl<URIAndBTreeClassImpl> minHeap;
    private int maxDocs;
    private int maxDocBytes;
    private int byteCounter;
    private long originalTimeStamp;
    private File baseDir;
    private ArrayList<URI> onDiskList;


    public DocumentStoreImpl() {
        bTree = new BTreeImpl<>();
        defaultCompressionFormat = CompressionFormat.ZIP;
        commandStack = new StackImpl();
        comparator = new Comparator<URI>() {
            @Override
            public int compare(URI o2, URI o1) {
                return (o1.compareTo(o2));
            }
        };
        trie = new TrieImpl<URI>(comparator);

        searchWord = null;
        maxDocs = 10;
        maxDocBytes = 1000;
        minHeap = new MinHeapImpl<>();
        byteCounter = 0;
        originalTimeStamp = 0;
        this.baseDir = null;
        onDiskList = new ArrayList<>();
    }

    public DocumentStoreImpl(File baseDir) {
        bTree = new BTreeImpl<>(baseDir);
        defaultCompressionFormat = CompressionFormat.ZIP;
        commandStack = new StackImpl();
        comparator = new Comparator<URI>() {
            @Override
            public int compare(URI o2, URI o1) {
                return (o1.compareTo(o2));
            }
        };
        trie = new TrieImpl<URI>(comparator);

        searchWord = null;
        maxDocs = 10;
        maxDocBytes = 1000;
        minHeap = new MinHeapImpl<>();
        byteCounter = 0;
        originalTimeStamp = 0;
        this.baseDir = baseDir;
        onDiskList = new ArrayList<>();
    }

    public void setMaxDocumentCount(int limit) {
        this.maxDocs = limit;
        while (minHeap.size() > this.maxDocs) {
            URI minURI = minHeap.removeMin().uri;
            this.deleteDocFromEverywhere(minURI);
        }
    }

    public void setMaxDocumentBytes(int limit) {
        this.maxDocBytes = limit;
        while (this.byteCounter > this.maxDocBytes) {
            URI minURI = minHeap.removeMin().uri;
            this.deleteDocFromEverywhere(minURI);
        }
    }


    @Override
    public List<String> search(String keyword) {
        List<String> stringArrayList = new ArrayList<>();
        if (keyword == null) return stringArrayList;
        this.setSearchWord(keyword);
        Iterator<URI> iterator = trie.getAllSorted(keyword).listIterator();
        while (iterator.hasNext()) {
            URI uri = iterator.next();
            stringArrayList.add(bTree.get(uri).getUncompressedString());
            bTree.get(uri).setLastUseTime(System.currentTimeMillis());
            URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
            minHeap.reHeapify(uriAndBTreeClass);
        }
        return stringArrayList;
    }

    @Override
    public List<byte[]> searchCompressed(String keyword) {
        List<byte[]> byteArrayArrayList = new ArrayList<>();
        if (keyword == null) return byteArrayArrayList;
        this.setSearchWord(keyword);
        Iterator<URI> iterator = trie.getAllSorted(keyword).listIterator();
        while (iterator.hasNext()) {
            URI uri = iterator.next();
            byteArrayArrayList.add(bTree.get(uri).getDocument());
            bTree.get(uri).setLastUseTime(System.currentTimeMillis());
            URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
            minHeap.reHeapify(uriAndBTreeClass);
        }
        return byteArrayArrayList;
    }


    protected void setSearchWord(String s) {
        this.searchWord = s;
    }

    public void setDefaultCompressionFormat(DocumentStore.CompressionFormat format) {
        this.defaultCompressionFormat = format;
    }

    private void deleteDocFromEverywhere(URI uriToRemove) {
        byteCounter = byteCounter - bTree.get(uriToRemove).getDocument().length;

        try {
            bTree.moveToDisk(uriToRemove);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onDiskList.add(uriToRemove);
    }

    private void makeSpaceForDoc(URI uri) {
        this.deleteDocFromEverywhere(uri);
    }

    public int putDocument(InputStream input, URI uri) {
        int hashCode = -1;
        try {
            if (this.defaultCompressionFormat != CompressionFormat.ZIP) {
                putDocument(input, uri, this.defaultCompressionFormat);
                return 0;
            }
            String s = IOUtils.toString(input);
            Map<String, Integer> hashMap = new HashMap<>();

            this.addToWWordCountMap(s, hashMap);
            hashCode = s.hashCode();
            if (bTree.get(uri) == null || bTree.get(uri).hashCode() != hashCode) {
                byte[] compressedDoc = this.zipCompression(s);
                DocumentImpl document = new DocumentImpl(uri, hashCode, CompressionFormat.ZIP, compressedDoc, hashMap, s);
                String[] inputWordStringArray = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+");
                for (int i = 0; i < inputWordStringArray.length; i++) {
                    trie.put(inputWordStringArray[i], uri);
                }
                if (document != null) document.setLastUseTime(System.currentTimeMillis());
                this.originalTimeStamp = System.currentTimeMillis();
                if (document.getDocument().length > maxDocBytes) throw new IllegalArgumentException("Document contains more bytes than store can hold.");
                URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
                minHeap.insert(uriAndBTreeClass);
                minHeap.reHeapify(uriAndBTreeClass);
                byteCounter = byteCounter + document.getDocument().length;
                bTree.put(uri, document);
                if (minHeap.size() > maxDocs) this.deleteDocFromEverywhere(minHeap.removeMin().uri);
                if (byteCounter > maxDocBytes) this.makeSpaceForDoc(minHeap.removeMin().uri);
                InputStream inputStream = IOUtils.toInputStream(s);
                Function<URI, Boolean> undoFunction = (URI) -> this.deleteDocumentForFunction(uri);
                Function<URI, Boolean> redoFunction = (URI) -> {
                    this.putDocumentForFunctionDefault(s, uri);
                    return true;
                };
                Command command = new Command(uri, undoFunction, redoFunction);
                commandStack.push(command);
            } else return hashCode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashCode;
    }

    public int putDocument(InputStream input, URI uri, DocumentStore.CompressionFormat format) {
        int hashCode = -1;
        try {
            if (format == CompressionFormat.ZIP) {
                putDocument(input, uri);
                return 0;
            }
            String s = IOUtils.toString(input);
            Map<String, Integer> map = new HashMap<>();
            this.addToWWordCountMap(s, map);
            hashCode = s.hashCode();
            if (bTree.get(uri) == null || bTree.get(uri).hashCode() != hashCode) {
                DocumentImpl document = null;
                if (format == CompressionFormat.GZIP) {
                    byte[] compressedDoc = this.gzipCompression(s);
                    document = new DocumentImpl(uri, hashCode, format, compressedDoc, map, s);
                }
                if (format == CompressionFormat.BZIP2) {
                    byte[] compressedDoc = this.bZip2Compression(s);
                    document = new DocumentImpl(uri, hashCode, format, compressedDoc, map, s);
                }
                if (format == CompressionFormat.JAR) {
                    byte[] compressedDoc = this.jarCompression(s);
                    document = new DocumentImpl(uri, hashCode, format, compressedDoc, map, s);
                }
                if (format == CompressionFormat.SEVENZ) {
                    return -1;
                }
                if (document == null) return -1;
                InputStream inputStream = IOUtils.toInputStream(s);
                Function<URI, Boolean> undoFunction = (URI) -> this.deleteDocumentForFunction(uri);
                Function<URI, Boolean> redoFunction = (URI) -> {
                    this.putDocumentForFunction(s, uri, format);
                    return true;
                };
                Command command = new Command(uri, undoFunction, redoFunction);
                commandStack.push(command);
                String[] inputWordStringArray = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+");
                for (int i = 0; i < inputWordStringArray.length; i++) {
                    trie.put(inputWordStringArray[i], uri);
                }
                if (document != null) document.setLastUseTime(System.currentTimeMillis());
                this.originalTimeStamp = System.currentTimeMillis();
                if (document.getDocument().length > maxDocBytes)
                    throw new IllegalArgumentException("Document contains more bytes than store can hold.");
                URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
                minHeap.insert(uriAndBTreeClass);
                minHeap.reHeapify(uriAndBTreeClass);
                byteCounter = byteCounter + document.getDocument().length;
                bTree.put(uri, document);
                if (minHeap.size() > maxDocs) this.deleteDocFromEverywhere(minHeap.removeMin().uri);
                if (byteCounter > maxDocBytes) this.makeSpaceForDoc(minHeap.removeMin().uri);
                bTree.put(uri, document);
                return hashCode;
            } else return hashCode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashCode;
    }

    private int putDocumentForFunction(String s, URI uri, DocumentStore.CompressionFormat format) {
        int hashCode = -1;
        InputStream inputStream = IOUtils.toInputStream(s);
        hashCode = s.hashCode();
        if (format == CompressionFormat.ZIP) {
            putDocumentForFunctionDefault(s, uri);
            return 0;
        }
        if (bTree.get(uri) == null || bTree.get(uri).hashCode() != hashCode) {
            DocumentImpl document = null;
            HashMap<String, Integer> hashMap = new HashMap<>();
            if (format == CompressionFormat.GZIP) {
                byte[] compressedDoc = this.gzipCompression(s);
                document = new DocumentImpl(uri, hashCode, format, compressedDoc, hashMap, s);
            }
            if (format == CompressionFormat.BZIP2) {
                byte[] compressedDoc = this.bZip2Compression(s);
                document = new DocumentImpl(uri, hashCode, format, compressedDoc, hashMap, s);
            }
            if (format == CompressionFormat.JAR) {
                byte[] compressedDoc = this.jarCompression(s);
                document = new DocumentImpl(uri, hashCode, format, compressedDoc, hashMap, s);
            }
            //if (format == CompressionFormat.SEVENZ) {}
            String[] inputWordStringArray = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (int i = 0; i < inputWordStringArray.length; i++) {
                trie.put(inputWordStringArray[i], uri);
            }
            if (document.getDocument().length > maxDocBytes)
                throw new IllegalArgumentException("Document contains more bytes than store can hold.");
            URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
            minHeap.insert(uriAndBTreeClass);
            byteCounter = byteCounter + document.getDocument().length;
            bTree.put(uri, document);
            if (minHeap.size() > maxDocs) this.deleteDocFromEverywhere(minHeap.removeMin().uri);
            if (byteCounter > maxDocBytes) this.makeSpaceForDoc(minHeap.removeMin().uri);
            document.setLastUseTime(this.originalTimeStamp);
            minHeap.reHeapify(uriAndBTreeClass);
            return hashCode;
        } else return hashCode;
    }

    private int putDocumentForFunctionDefault(String s, URI uri) {
        int hashCode = -1;
        hashCode = s.hashCode();
        if (this.defaultCompressionFormat != CompressionFormat.ZIP) {
            putDocumentForFunction(s, uri, this.defaultCompressionFormat);
            return 0;
        }
        if (bTree.get(uri) == null || bTree.get(uri).hashCode() != hashCode) {
            byte[] compressedDoc = this.zipCompression(s);
            HashMap<String, Integer> hashMap = new HashMap<>();
            DocumentImpl document = new DocumentImpl(uri, hashCode, CompressionFormat.ZIP, compressedDoc, hashMap, s);
            String[] inputWordStringArray = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (int i = 0; i < inputWordStringArray.length; i++) {
                trie.put(inputWordStringArray[i], uri);
            }
            if (document.getDocument().length > maxDocBytes) throw new IllegalArgumentException("Document contains more bytes than store can hold.");
            URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
            minHeap.insert(uriAndBTreeClass);
            byteCounter = byteCounter + document.getDocument().length;
            bTree.put(uri, document);
            if (minHeap.size() > maxDocs) this.deleteDocFromEverywhere(minHeap.removeMin().uri);
            if (byteCounter > maxDocBytes) this.makeSpaceForDoc(minHeap.removeMin().uri);
            document.setLastUseTime(this.originalTimeStamp);
            minHeap.reHeapify(uriAndBTreeClass);
            return hashCode;
        } else return hashCode;
    }

    private Map<String, Integer> addToWWordCountMap(String docString, Map<String, Integer> map) {
        String[] inputWordStringArray = docString.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+");
        for (int i = 0; i < inputWordStringArray.length; i++) {
            if (!(map.containsKey(inputWordStringArray[i]))) {
                map.put(inputWordStringArray[i], 1);
            } else {
                int x = map.get(inputWordStringArray[i]);
                map.put(inputWordStringArray[i], x + 1);
            }
        }
        return map;
    }


    public String getDocument(URI uri) {
        if (uri == null) throw new IllegalArgumentException("URI passed is null.");
        if (bTree.get(uri) == null) return null;
        URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
        if (onDiskList.contains(uri)) {
            byteCounter = byteCounter + bTree.get(uri).getDocument().length;
            minHeap.insert(uriAndBTreeClass);
            bTree.get(uri).setLastUseTime(System.currentTimeMillis());
            minHeap.reHeapify(uriAndBTreeClass);
            if (minHeap.size() > maxDocs) {
                deleteDocFromEverywhere(minHeap.removeMin().uri);
            }
            if (byteCounter > maxDocBytes) {
                deleteDocFromEverywhere(minHeap.removeMin().uri);
            }
            onDiskList.remove(uri);
        }
        else {
            bTree.get(uri).setLastUseTime(System.currentTimeMillis());
            minHeap.reHeapify(uriAndBTreeClass);
        }
        if (bTree.get(uri).getCompressionFormat() == CompressionFormat.ZIP) {
            return this.zipDecompression(uri);
        }
        if (bTree.get(uri).getCompressionFormat() == CompressionFormat.JAR) {
            return this.jarDecompression(uri);
        }
        if (bTree.get(uri).getCompressionFormat() == CompressionFormat.GZIP) {
            return this.gzipDecompression(uri);
        }
        if (bTree.get(uri).getCompressionFormat() == CompressionFormat.BZIP2) {
            return this.bZip2Decompression(uri);
        }
        if (bTree.get(uri).getCompressionFormat() == CompressionFormat.SEVENZ) {
            return this.sevenZDecompression(uri);
        }
        return "";
    }


    public byte[] getCompressedDocument(URI uri) {
        if (uri == null) throw new IllegalArgumentException("URI passed is null.");
        if (bTree.get(uri) == null) return null;
        URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
        if (onDiskList.contains(uri)) {
            byteCounter = byteCounter + bTree.get(uri).getDocument().length;
            minHeap.insert(uriAndBTreeClass);
            bTree.get(uri).setLastUseTime(System.currentTimeMillis());
            minHeap.reHeapify(uriAndBTreeClass);
            if (minHeap.size() > maxDocs) {
                deleteDocFromEverywhere(minHeap.removeMin().uri);
            }
            if (byteCounter > maxDocBytes) {
                deleteDocFromEverywhere(minHeap.removeMin().uri);
            }
            onDiskList.remove(uri);
        }
        bTree.get(uri).setLastUseTime(System.currentTimeMillis());
        minHeap.reHeapify(uriAndBTreeClass);
        return bTree.get(uri).getDocument();
    }

    public boolean deleteDocument(URI uri) {
        if (bTree.get(uri) == null) return false;
        else {
            String s = this.getDocument(uri);
            CompressionFormat format = bTree.get(uri).getCompressionFormat();
            Function<URI, Boolean> undoFunction = (URI) -> {
                if (format == defaultCompressionFormat) {
                    this.putDocumentForFunctionDefault(s, uri);
                    return true;
                } else {
                    this.putDocumentForFunction(s, uri, format);
                    return true;
                }
            };
            Function<URI, Boolean> redoFunction = (URI) -> this.deleteDocumentForFunction(uri);
            Command command = new Command(uri, undoFunction, redoFunction);
            commandStack.push(command);
            String[] deleteWordStringArray = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (int i = 0; i < deleteWordStringArray.length; i++) {
                trie.delete(deleteWordStringArray[i], uri);
            }
            bTree.get(uri).setLastUseTime(0L);
            URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
            minHeap.reHeapify(uriAndBTreeClass);
            minHeap.removeMin();
            byteCounter = byteCounter - bTree.get(uri).getDocument().length;
            bTree.put(uri, null);
        }
        return true;
    }


    private boolean deleteDocumentForFunction(URI uri) {
        if (bTree.get(uri) == null) return false;
        else {
            String s = this.getDocument(uri);
            String[] deleteWordStringArray = s.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (int i = 0; i < deleteWordStringArray.length; i++) {
                trie.delete(deleteWordStringArray[i], uri);
            }
            bTree.get(uri).setLastUseTime(0L);
            URIAndBTreeClassImpl uriAndBTreeClass = new URIAndBTreeClassImpl(uri, bTree);
            minHeap.reHeapify(uriAndBTreeClass);
            minHeap.removeMin();
            byteCounter = byteCounter - bTree.get(uri).getDocument().length;
            bTree.put(uri, null);
            return true;
        }
    }

    public boolean undo() throws IllegalStateException {
        Command command = commandStack.pop();
        return command.undo();
    }

    public boolean undo(URI uri) throws IllegalStateException {
        //put all commands before the last command of given URI into a tempStack
        StackImpl<Command> tempStack = new StackImpl<>();
        if (commandStack.peek().getUri() == uri) {
            commandStack.pop().undo();
            return true;
        }
        while (commandStack.size() > 0 && commandStack.peek().getUri() != uri) {
            Command command = commandStack.pop();
            command.undo();
            tempStack.push(command);
        }
        //undo the last command of the given URI
        if (commandStack.size() > 0) {
            commandStack.pop().undo();
        }
        //put the commands in the tempStack back
        if (tempStack.size() > 0) {
            for (int i = 0; i <= tempStack.size(); i++) {
                Command command = tempStack.pop();
                command.redo();
                commandStack.push(command);
            }
        }
        return true;
    }

    private byte[] zipCompression(String s) {
        byte[] compressed = null;
        try {
            OutputStream baos = new ByteArrayOutputStream();
            ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, baos);
            aos.putArchiveEntry(new ZipArchiveEntry(""));
            aos.write(s.getBytes());
            aos.closeArchiveEntry();
            aos.close();
            compressed = ((ByteArrayOutputStream) baos).toByteArray();
        } catch (IOException | org.apache.commons.compress.archivers.ArchiveException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    private String zipDecompression(URI uri) {
        String decompressed = "";
        try {
            byte[] compressed = this.getCompressedDocument(uri);
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

    private byte[] jarCompression(String s) {
        byte[] compressed = null;
        try {
            OutputStream baos = new ByteArrayOutputStream();
            ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.JAR, baos);
            aos.putArchiveEntry(new ZipArchiveEntry(""));
            aos.write(s.getBytes());
            aos.closeArchiveEntry();
            aos.close();
            compressed = ((ByteArrayOutputStream) baos).toByteArray();
        } catch (IOException | org.apache.commons.compress.archivers.ArchiveException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    private String jarDecompression(URI uri) {
        String decompressed = "";
        try {
            byte[] compressed = this.getCompressedDocument(uri);
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

    private byte[] gzipCompression(String s) {
        byte[] compressed = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(baos);
            gzipOut.write(s.getBytes());
            gzipOut.close();
            compressed = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    private String gzipDecompression(URI uri) {
        String decompressed = "";
        try {
            byte[] compressed = this.getCompressedDocument(uri);
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

    private byte[] bZip2Compression(String s) {
        byte[] compressed = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BZip2CompressorOutputStream bZip2Out = new BZip2CompressorOutputStream(baos);
            bZip2Out.write(s.getBytes());
            bZip2Out.close();
            compressed = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    private String bZip2Decompression(URI uri) {
        String decompressed = "";
        try {
            byte[] compressed = this.getCompressedDocument(uri);
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

    private byte[] sevenZCompression(String s) {
        byte[] compressed = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BZip2CompressorOutputStream bZip2Out = new BZip2CompressorOutputStream(baos);
            bZip2Out.write(s.getBytes());
            bZip2Out.close();
            compressed = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    private String sevenZDecompression(URI uri) {
        String decompressed = "";
        try {
            byte[] compressed = this.getCompressedDocument(uri);
            SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(compressed);
            SevenZFile sevenZFile = new SevenZFile(inMemoryByteChannel);
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            sevenZFile.read();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return decompressed;
    }

    protected boolean isOnDisk(URI uri) {
        return this.onDiskList.contains(uri);
    }

    public static void main(String[] args) {

        File file = new File("/Users/alexanderschlesinger/Desktop/GIT/SchlesingerAlexander/DataStructures/project/stage1");

        DocumentStoreImpl dsi = new DocumentStoreImpl(file);
        String str = "http://testfolder/myfile";
        URI uri = URI.create(str);
        String source1 = "String 1";
        ByteArrayInputStream in1 = new ByteArrayInputStream(source1.getBytes());
        dsi.putDocument(in1, uri, CompressionFormat.ZIP);




        //create and add doc1
        String str1 = "this is doc#1";

        URI uri1 = null;
        try {
            uri1 = new URI("http://www.yu.edu/doc1");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(str1.getBytes());
        dsi.putDocument(bis, uri1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dsi.setMaxDocumentCount(1);

        System.out.println(dsi.getDocument(uri));



    }

}