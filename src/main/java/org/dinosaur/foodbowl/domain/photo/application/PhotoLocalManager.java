package org.dinosaur.foodbowl.domain.photo.application;

import java.io.File;
import java.io.IOException;
import org.dinosaur.foodbowl.domain.photo.domain.vo.PhotoName;
import org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoLocalManager implements PhotoManager {

    private static final String SLASH = File.separator;
    private static final String SYSTEM_PATH = System.getProperty("user.dir");

    private final String url;
    private final String fileDirectory;

    public PhotoLocalManager(
            @Value("${openapi.dev_url}") String url,
            @Value("${file.dir}") String fileDirectory
    ) {
        this.url = url;
        this.fileDirectory = fileDirectory;
    }

    public String upload(MultipartFile image, String workingDirectory) {
        File directory = loadDirectory(getFileStorePath(workingDirectory));
        if (isEmptyFile(image)) {
            throw new FileException(FileExceptionType.EMPTY);
        }
        String saveFileName = PhotoName.of(image.getOriginalFilename());
        File uploadPath = new File(directory, saveFileName);
        transferFile(image, uploadPath);
        return getFileFullPath(workingDirectory, saveFileName);
    }

    private String getFileStorePath(String workingDirectory) {
        return SYSTEM_PATH + SLASH + fileDirectory + SLASH + workingDirectory;
    }

    private String getFileFullPath(String workingDirectory, String fileName) {
        return url + SLASH + fileDirectory + SLASH + workingDirectory + SLASH + fileName;
    }

    private File loadDirectory(String directoryLocation) {
        File directory = new File(directoryLocation);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private boolean isEmptyFile(MultipartFile multipartFile) {
        return multipartFile == null || multipartFile.isEmpty();
    }

    private void transferFile(MultipartFile file, File uploadPath) {
        try {
            file.transferTo(uploadPath);
        } catch (IOException e) {
            throw new FileException(FileExceptionType.TRANSFER, e);
        }
    }

    public void delete(String path) {
        String deletePath = getFileLocalPath(path);
        File file = new File(deletePath);
        deleteFile(file);
    }

    private String getFileLocalPath(String fullPath) {
        int urlIndex = fullPath.lastIndexOf(url);
        if (urlIndex == -1) {
            throw new FileException(FileExceptionType.NAME);
        }
        int urlNextIndex = urlIndex + url.length();
        return SYSTEM_PATH + fullPath.substring(urlNextIndex);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
