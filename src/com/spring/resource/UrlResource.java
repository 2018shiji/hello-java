package com.spring.resource;

import com.spring.util.ResourceUtils;
import com.spring.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class UrlResource extends AbstractUrlResource {

    private final URI uri;
    private final URL url;

    public UrlResource(URL url) {
        this.url = url;
        this.uri = null;
    }

    @Override
    public String getDescription() {
        return "URL [" + this.url + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection conn = this.url.openConnection();
        ResourceUtils.useCachesIfNecessary(conn);
        try {
            return conn.getInputStream();
        } catch (IOException e) { throw e; }
    }

    @Override
    public URL getURL() throws IOException {
        return this.url;
    }

    @Override
    public String getFileName() {
        return StringUtils.getFileName(url.getPath());
    }

}
