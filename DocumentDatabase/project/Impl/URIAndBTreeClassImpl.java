package edu.yu.cs.com1320.project.Impl;


import edu.yu.cs.com1320.project.Document;
import edu.yu.cs.com1320.project.DocumentStore;
import edu.yu.cs.com1320.project.DocumentStore.CompressionFormat;

import java.net.URI;


public class URIAndBTreeClassImpl implements Comparable<URIAndBTreeClassImpl> {

URI uri;
BTreeImpl<URI, DocumentImpl> bTree;


public URIAndBTreeClassImpl(URI uri, BTreeImpl bTree) {
    this.uri = uri;
    this.bTree = bTree;
}


    @Override
    public int compareTo(URIAndBTreeClassImpl o) {
        if( this.bTree.get(o.uri) != null) {
            if (this.bTree.get(uri).getLastUseTime() > this.bTree.get(o.uri).getLastUseTime()) return 1;
            if (this.bTree.get(uri).getLastUseTime() < this.bTree.get(o.uri).getLastUseTime()) return -1;
            else return 0;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof URIAndBTreeClassImpl)) return false;
        URIAndBTreeClassImpl a = (URIAndBTreeClassImpl) o;
        return this.uri.equals(a.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }



}

	
