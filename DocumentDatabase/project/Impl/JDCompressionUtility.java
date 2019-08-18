package edu.yu.cs.com1320.project.Impl;

import edu.yu.cs.com1320.project.DocumentStore;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class JDCompressionUtility
{
    public static Path path;
    public byte[] compress(DocumentStore.CompressionFormat format, String docStr, String name) throws IOException
    {
        switch(format)
        {
            case ZIP:
                return compressAsZip(docStr,name);
            case SEVENZ:
                return compressAs7zip(docStr,name);
            case GZIP:
                return compressAsGzip(docStr);
            case JAR:
                return compressAsJar(docStr,name);
            case BZIP2:
                return compressAsBzip2(docStr);
        }
        return null;
    }

    public static byte[] compressAsZip(String docStr, String name) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(bos);
        ZipArchiveEntry entry = new ZipArchiveEntry(name);
        byte[] content = docStr.getBytes();
        entry.setSize(content.length);
        zipOutput.putArchiveEntry(entry);
        zipOutput.write(content);
        zipOutput.closeArchiveEntry();
        zipOutput.flush();
        zipOutput.finish();
        zipOutput.close();
        return bos.toByteArray();
    }

    public static void writeToFile(byte[] bytes, File file) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }

    public static byte[] compressAs7zip(String docStr,String name) throws IOException
    {
        if(path == null)
        {
            path = Files.createTempDirectory("foo", new FileAttribute[0]);
        }
        File tempIn = new File(path.toFile(),"7zip.in");
        writeToFile(docStr.getBytes(),tempIn);
        File tempOut = new File(path.toFile(),"7zip.out");

        SevenZOutputFile sevenZOutput = new SevenZOutputFile(tempOut);
        SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(tempIn, name);
        sevenZOutput.putArchiveEntry(entry);
        sevenZOutput.write(docStr.getBytes());
        sevenZOutput.closeArchiveEntry();
        sevenZOutput.finish();
        sevenZOutput.close();
        FileInputStream fis = new FileInputStream(tempOut);
        byte[] compressed = fis.readAllBytes();
        fis.close();
        return compressed;
    }

    public static byte[] compressAsGzip(String docStr) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bos);
        gzOut.write(docStr.getBytes());
        gzOut.flush();
        gzOut.finish();
        gzOut.close();
        return bos.toByteArray();
    }

    public static byte[] compressAsJar(String docStr, String name) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JarArchiveOutputStream jos = new JarArchiveOutputStream(bos);
        jos.putArchiveEntry(new JarArchiveEntry(name));
        jos.write(docStr.getBytes());
        jos.closeArchiveEntry();
        jos.flush();
        jos.finish();
        jos.close();
        return bos.toByteArray();
    }

    public static byte[] compressAsBzip2(String docStr) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream gzOut = new BZip2CompressorOutputStream(bos);
        gzOut.write(docStr.getBytes());
        gzOut.flush();
        gzOut.finish();
        gzOut.close();
        return bos.toByteArray();
    }

    public String decompress(DocumentStore.CompressionFormat format, byte[] doc) throws IOException
    {
        switch(format)
        {
            case ZIP:
                return decompressAsZip(doc);
            case SEVENZ:
                return decompressAs7zip(doc);
            case GZIP:
                return decompressAsGzip(doc);
            case JAR:
                return decompressAsJar(doc);
            case BZIP2:
                return decompressAsBzip2(doc);
        }
        return null;
    }

    public static String decompressAsZip(byte[] doc) throws IOException
    {
        SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(doc);
        ZipFile zipFile = new ZipFile(inMemoryByteChannel);
        ZipArchiveEntry archiveEntry = zipFile.getEntries().nextElement();
        InputStream inputStream = zipFile.getInputStream(archiveEntry);
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        return new String(bytes);
    }

    public static String decompressAs7zip(byte[] doc) throws IOException
    {
        SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(doc);
        SevenZFile sevenZFile = new SevenZFile(inMemoryByteChannel);
        SevenZArchiveEntry entry = sevenZFile.getNextEntry();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int n = 0;
        while (-1 != (n = sevenZFile.read())) {
            bos.write(n);
        }
        sevenZFile.close();
        return new String(bos.toByteArray());
    }

    public static String decompressAsGzip(byte[] doc) throws IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(doc);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(bis);
        byte[] bytes = gzIn.readAllBytes();
        gzIn.close();
        return new String(bytes);
    }

    public static String decompressAsJar(byte[] doc) throws IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(doc);
        JarArchiveInputStream jis = new JarArchiveInputStream(bis);
        JarArchiveEntry entry = jis.getNextJarEntry();
        byte[] bytes = jis.readAllBytes();
        jis.close();
        return new String(bytes);
    }

    public static String decompressAsBzip2(byte[] doc) throws IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(doc);
        BZip2CompressorInputStream bzin = new BZip2CompressorInputStream(bis);
        byte[] bytes = bzin.readAllBytes();
        bzin.close();
        return new String(bytes);
    }
}
