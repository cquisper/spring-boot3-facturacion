package com.cquisper.springboot.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadFileServiceImpl implements UploadFileService{
    private static final String DIRECTORY_UPLOAD = "uploads";

    private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);

    @Override
    public Resource cargar(String nombre) throws MalformedURLException {
        Path rutaImagen = getPath(nombre);
        Resource recurso = new UrlResource(rutaImagen.toUri());

        if(!recurso.isReadable() || !recurso.exists()){
            throw new RuntimeException("Error: no se puede cargar la imagen: " + nombre);
        }
        try {
            log.info(recurso.getURI().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return recurso;
    }

    @Override
    public String guardar(MultipartFile archivo) throws IOException {
        String nombreImagen = "";
        if(!archivo.isEmpty()){
            nombreImagen = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path rutaImagen = getPath(nombreImagen);
            Files.copy(archivo.getInputStream(), rutaImagen);
            log.info("Guardando imagen: " + nombreImagen);
        }
        return nombreImagen;
    }

    @Override
    public boolean eliminar(String nombre) {
        Path rutaImagenEliminar = getPath(nombre);
        File archivoImagen = rutaImagenEliminar.toFile();
        if(archivoImagen.exists() && archivoImagen.canRead()){
            log.info("Eliminando imagen: " + nombre);
            return archivoImagen.delete();
        }
        return false;
    }

    @Override
    public void deleteAll() {
        Path directory = Paths.get(DIRECTORY_UPLOAD);
        log.info(directory.toAbsolutePath().toString());
        FileSystemUtils.deleteRecursively(directory.toFile()); //Elimina los archivos de forma recursiva y tbm el directorio
    }

    @Override
    public void init() throws IOException {
        Files.createDirectory(Paths.get(DIRECTORY_UPLOAD));
    }

    @Override
    public Path getPath(String nombre) {
        return Paths.get(DIRECTORY_UPLOAD).resolve(nombre).toAbsolutePath();
    }
}
