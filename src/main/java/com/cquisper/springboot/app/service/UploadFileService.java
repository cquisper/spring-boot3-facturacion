package com.cquisper.springboot.app.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface UploadFileService {
    Resource cargar(String nombre) throws MalformedURLException;

    String guardar(MultipartFile archivo) throws IOException;

    boolean eliminar(String nombre);

    void deleteAll();

    void init() throws IOException;

    Path getPath(String nombre);
}
