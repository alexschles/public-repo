package edu.yu.cs.com1320.project;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

public interface DocumentStore
{
    static enum CompressionFormat{
      ZIP,JAR,SEVENZ,GZIP,BZIP2
    };

    /**
     * Retrieve all documents that contain the given key word.
     * Documents are returned in sorted, in descending order, by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return
     */
    List<String> search(String keyword);

    /**
     * Retrieve the compressed form of all documents that contain the given key word.
     * Documents are returned in sorted, in descending order, by the number of times the keyword appears in the document
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return
     */
    List<byte[]> searchCompressed(String keyword);

    /**
     * specify which compression format should be used if none is specified on a putDocument,
     * either because the two-argument putDocument method is used or the CompressionFormat is
     * null in the three-argument version of putDocument
     * @param format
     * @see DocumentStore#putDocument(InputStream, URI)
     * @see DocumentStore#putDocument(InputStream, URI, CompressionFormat)
     */
    void setDefaultCompressionFormat(CompressionFormat format);

    default CompressionFormat getDefaultCompressionFormat(){
        return CompressionFormat.ZIP;
    }
    /**
     * since the user does not specify a compression format, use the default compression format
     * @param input the document being put
     * @param uri unique identifier for the document
     * @return the hashcode of the document
     */
    int putDocument(InputStream input, URI uri);

    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format compression format to use for compressing this document
     * @return the hashcode of the document
     */
    int putDocument(InputStream input, URI uri, CompressionFormat format);

    /**
     * @param uri the unique identifier of the document to getAll
     * @return the <B>uncompressed</B> document as a String, or null if no such document exists
     */
    String getDocument(URI uri);

    /**
     * @param uri the unique identifier of the document to getAll
     * @return the <B>compressed</B> version of the document
     */
    byte[] getCompressedDocument(URI uri);

    /**
     * @param uri the unique identifier of the document to deleteAll
     * @return true if the document is deleted, false if no document exists with that URI
     */
    boolean deleteDocument(URI uri);

    /**
     *
     * DO NOT IMPLEMENT IN STAGE 1 OF THE PROJECT. THIS IS FOR STAGE 2.
     *
     * undo the last put or deleteAll command
     * @return true if successfully undid command, false if not successful
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    boolean undo() throws IllegalStateException;

    /**
     *
     * DO NOT IMPLEMENT IN STAGE 1 OF THE PROJECT. THIS IS FOR STAGE 2.
     *
     * undo the last put or deleteAll that was done with the given URI as its key
     * @param uri
     * @return
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    boolean undo(URI uri) throws IllegalStateException;


    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    void setMaxDocumentCount(int limit);


    /**
     * set maximum number of bytes of memory that may be used by all the compressed
     documents in memory combined
     * @param limit
     */
    void setMaxDocumentBytes(int limit);
}


