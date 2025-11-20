package vn.techzone.khieu.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.techzone.khieu.utils.error.StorageException;

@Service
public class FileService {

    @Value("${upload-file.base-url}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(baseURI + folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println("CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + baseURI + folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException,
            IOException {
        // create unique filename
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(baseURI + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    public void delete(String folder, String fileName) throws StorageException, URISyntaxException {
        try {
            URI uri = new URI(baseURI + folder + "/" + fileName);
            Path filePath = Paths.get(uri);

            if (!Files.exists(filePath)) {
                throw new StorageException("File not found: " + fileName);
            }

            Files.delete(filePath);
        } catch (IOException e) {
            throw new StorageException("Cannot delete file: " + fileName);
        }
    }

}