package vn.iotstar.service.impl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.config.StorageProperties;
import vn.iotstar.exception.StorageException;
import vn.iotstar.service.IStorageService;

@Service
public class FileSystemStorageServiceImpl implements IStorageService {
    private final Path rootLocation;

    public FileSystemStorageServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
    }

    @Override
    public String getSorageFilename(MultipartFile file, String id) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!ext.matches("[A-Za-z0-9]{1,10}")) {
            throw new StorageException("Invalid file extension");
        }
        return "p" + id + (ext.isBlank() ? "" : "." + ext);
    }

    @Override
    public void store(MultipartFile file, String storeFilename) {
        try {
            if (file == null || file.isEmpty()) {
                throw new StorageException("Failed to store empty file");
            }
            Path destinationFile = rootLocation.resolve(Paths.get(storeFilename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(rootLocation)) {
                throw new StorageException("Cannot store file outside current directory");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            if (e instanceof StorageException storageException) {
                throw storageException;
            }
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new StorageException("Cannot read file: " + filename);
        } catch (Exception e) {
            throw new StorageException("Could not read file: " + filename, e);
        }
    }

    @Override
    public Path load(String filename) {
        Path file = rootLocation.resolve(filename).normalize();
        if (!file.getParent().equals(rootLocation)) {
            throw new StorageException("Invalid filename");
        }
        return file;
    }

    @Override
    public void delete(String storeFilename) throws Exception {
        if (storeFilename == null || storeFilename.isBlank()) return;
        Files.deleteIfExists(load(storeFilename));
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (Exception e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
