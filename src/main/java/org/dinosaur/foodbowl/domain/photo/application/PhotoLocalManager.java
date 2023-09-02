package org.dinosaur.foodbowl.domain.photo.application;

import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_NAME;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_READ;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_TRANSFER;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.dinosaur.foodbowl.domain.photo.domain.vo.PhotoName;
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

    public List<String> upload(List<MultipartFile> files, String workingDirectory) {
        File directory = loadDirectory(getImageStorePath(workingDirectory));

        List<String> filePaths = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            if (isEmptyFileOrNotImage(multipartFile)) {
                continue;
            }

            String saveFileName = PhotoName.of(multipartFile.getOriginalFilename());
            File uploadPath = new File(directory, saveFileName);
            transferFile(multipartFile, uploadPath);
            filePaths.add(getImageFullPath(workingDirectory, saveFileName));
        }
        return filePaths;
    }

    private String getImageStorePath(String workingDirectory) {
        return SYSTEM_PATH + SLASH + fileDirectory + SLASH + workingDirectory;
    }

    private String getImageFullPath(String workingDirectory, String fileName) {
        return url + SLASH + fileDirectory + SLASH + workingDirectory + SLASH + fileName;
    }

    private File loadDirectory(String directoryLocation) {
        File directory = new File(directoryLocation);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private boolean isEmptyFileOrNotImage(MultipartFile multipartFile) {
        return multipartFile == null || multipartFile.isEmpty() || isNotImageFile(multipartFile);
    }

    private boolean isNotImageFile(MultipartFile file) {
        try (InputStream originalInputStream = new BufferedInputStream(file.getInputStream())) {
            return ImageIO.read(originalInputStream) == null;
        } catch (IOException e) {
            throw new FileException(FILE_READ);
        }
    }

    private void transferFile(MultipartFile file, File uploadPath) {
        try {
            file.transferTo(uploadPath);
        } catch (IOException e) {
            throw new FileException(FILE_TRANSFER, e);
        }
    }

    public void delete(List<String> paths) {
        for (String path : paths) {
            String deletePath = getImageLocalPath(path);
            File file = new File(deletePath);
            deleteFile(file);
        }
    }

    private String getImageLocalPath(String fullPath) {
        int urlIndex = fullPath.lastIndexOf(url);

        if (urlIndex == -1) {
            throw new FileException(FILE_NAME);
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
