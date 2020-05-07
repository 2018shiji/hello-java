package com.spring.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class AbstractFileResource implements FileResource {
    @Override
    public boolean exists() {
        if(isFile()){
            try {
                return getFile().exists();
            } catch (IOException e) {e.printStackTrace();}
        }

        try{
            getInputStream().close();
            return true;
        } catch (IOException e) {e.printStackTrace();}

        return false;
    }

    @Override//交由子类实现
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return new URI(url.toString());
        } catch (URISyntaxException e) {throw new IOException("invalid uri[" + url + "]", e);}
    }

    @Override//交由子类实现
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    @Override
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        try {
            int read;
            long size = 0;
            byte[] buf = new byte[256];

            while((read = is.read(buf)) != -1){ size += read; }

            return size;
        } finally {
            try {
                is.close();
            } catch (IOException e) { e.printStackTrace();}
        }
    }

    @Override
    public long lastModified() throws IOException {
        File fileToCheck = getFile();
        long lastModified = fileToCheck.lastModified();
        if (lastModified == 0L && !fileToCheck.exists()) {
            throw new FileNotFoundException(getDescription() +
                    " cannot be resolved in the file system for checking its last-modified timestamp");
        }
        return lastModified;
    }

    @Override
    public FileResource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("cannot create relative");
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public boolean equals( Object other) {
        return (this == other || (other instanceof FileResource &&
                ((FileResource) other).getDescription().equals(getDescription())));
    }

    @Override
    public int hashCode() {
        return getDescription().hashCode();
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
