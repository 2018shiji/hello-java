package com.spring.util;

import com.spring.resource.FileResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertiesLoaderUtils {
    private static final String XML_FILE_EXTENSION = ".xml";

    public static Properties loadProperties(FileResource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }

    public static void fillProperties(Properties props, FileResource resource) throws IOException {
        InputStream is = resource.getInputStream();
        try {
            String fileName = resource.getFileName();
            if(fileName != null && fileName.endsWith(XML_FILE_EXTENSION))
                props.loadFromXML(is);
            else
                props.load(is);
        } finally {
            is.close();
        }
    }

}
