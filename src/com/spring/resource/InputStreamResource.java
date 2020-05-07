package com.spring.resource;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamResource {

    InputStream getInputStream() throws IOException;

}
