package com.spring.resource;

import com.spring.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;

/**
 * abstract base class for resources which resolve URLs into File references
 */
public abstract class AbstractUrlResource extends AbstractFileResource {

    @Override
    public boolean exists() {
        try{
            URL url = getURL();
            if(ResourceUtils.isFileURL(url)){
                return getFile().exists();
            }
            /** todo:remote connection of url */
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }

    @Override
    public boolean isReadable() {
        try {
            URL url = getURL();
            if(ResourceUtils.isFileURL(url)) {
                File file  = getFile();
                return (file.canRead() && !file.isDirectory());
            }
            /** todo:remote connection of url */
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }

    @Override
    public boolean isFile() {
        try{
            URL url = getURL();
            return ResourceUtils.URL_PROTOCOL_FILE.equals(url.getProtocol());
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }

    @Override
    public File getFile() throws IOException {
        URL url = getURL();
        return ResourceUtils.getFile(url, getDescription());
    }

    protected boolean isFile(URI uri) {
        return ResourceUtils.URL_PROTOCOL_FILE.equals(uri.getScheme());
    }

    protected File getFile(URI uri) throws IOException {
        return ResourceUtils.getFile(uri, getDescription());
    }

    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        try {
            return FileChannel.open(getFile().toPath(), StandardOpenOption.READ);
        } catch (FileNotFoundException | NoSuchFileException e) {
            return super.readableChannel();
        }
    }

    @Override
    public long contentLength() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url)) {
            // Proceed with file system resolution
            File file = getFile();
            long length = file.length();
            if (length == 0L && !file.exists()) {
                throw new FileNotFoundException(getDescription() +
                        " cannot be resolved in the file system for checking its content length");
            }
            return length;
        } else { return 0; }
    }

    @Override
    public long lastModified() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url)) {
            File fileToCheck = getFile();
            long lastModified = fileToCheck.lastModified();
            if (lastModified == 0L && !fileToCheck.exists()) {
                throw new FileNotFoundException(getDescription() +
                        " cannot be resolved in the file system for checking its last-modified timestamp");
            }
            return lastModified;
        } else { return 0; }
    }

    public String getFileName() {
        return null;
    }
}
