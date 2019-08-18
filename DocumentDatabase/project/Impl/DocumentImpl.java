package edu.yu.cs.com1320.project.Impl;

import edu.yu.cs.com1320.project.Document;
import edu.yu.cs.com1320.project.DocumentStore;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DocumentImpl implements Document {

    private byte[] compressedFile;
    private int hashCode;
    private URI uri;
    private DocumentStore.CompressionFormat compressionFormat;
    private Map<String, Integer> map;
    private long lastUseTime;
    private String uncompressedString;
    private boolean comingFromDisk;



    public DocumentImpl (URI uri, int hashCode, DocumentStore.CompressionFormat compressionFormat, byte[] compressedFile, Map<String, Integer> map, String s) {
        this.compressedFile = compressedFile;
        this.hashCode = hashCode;
        this.uri = uri;
        this.compressionFormat= compressionFormat;
        this.map = map;
        this.lastUseTime = 0L;
        this.uncompressedString = s;
        this.comingFromDisk = false;
    }


    public  byte[] getDocument() { return this.compressedFile; }

    public int getDocumentHashCode() {
        return this.hashCode;
    }

    public URI getKey() { return this.uri; }

    public long getLastUseTime() {return this.lastUseTime;}

    public DocumentStore.CompressionFormat getCompressionFormat() {
        return this.compressionFormat;
    }

    public void setLastUseTime(long timeInMilliseconds) { this.lastUseTime = timeInMilliseconds;}

    @Override
    public Map<String, Integer> getWordMap() {
        return this.map;
    }

    @Override
    public void setWordMap(Map<String, Integer> wordMap) {
        this.map = wordMap;

    }

    public int wordCount(String word) { return this.map.get(word.toLowerCase()); }

    protected void setComingFromDisk(boolean b) {
        this.comingFromDisk = b;
    }

    protected String getUncompressedString() {
        return this.uncompressedString;
    }

    @Override
    public int compareTo(Document o) {
        if( o != null) {
            if (this.getLastUseTime() > o.getLastUseTime()) return 1;
            if (this.getLastUseTime() < o.getLastUseTime()) return -1;
            else return 0;
        }
        return 0;
    }


    public static void main(String[] args) {
        String s = "   Hi my name is Alex, yes it's A lex and alEx";
        String b = "Alex";

    }


}
