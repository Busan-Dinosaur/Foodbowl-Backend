package org.dinosaur.foodbowl.domain.photo.application;

import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_BASE_NAME_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_EXTENSION_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_FORMAT_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_READ_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_TRANSFER_ERROR;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoLocalUploader implements PhotoUploader {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpeg", "jpg", "png", "webp");
    private static final String SLASH = File.separator;
    private static final String UNDER_BAR = "_";
    private static final String DOT = ".";

    private final String url;
    private final String fileDirectory;

    public PhotoLocalUploader(
            @Value("${openapi.dev_url}") String url,
            @Value("${file.dir}") String fileDirectory
    ) {
        this.url = url;
        this.fileDirectory = fileDirectory;
    }

    public List<String> upload(List<MultipartFile> files, String parentDirectory) {
        File directory =
                loadDirectory(System.getProperty("user.dir") + SLASH + fileDirectory + SLASH + parentDirectory);

        List<String> filePaths = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            if (isEmptyFileOrNotImage(multipartFile)) {
                continue;
            }

            String originalFilename = multipartFile.getOriginalFilename();
            validateFileName(originalFilename);
            String saveFileName = convertToPathWithName(originalFilename);

            File uploadPath = new File(directory, saveFileName);
            transferFile(multipartFile, uploadPath);
            filePaths.add(getImageFullPath(uploadPath.getPath()));
        }
        return filePaths;
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
            throw new FileException(FILE_READ_ERROR);
        }
    }

    private String convertToPathWithName(String originalFilename) {
        int lastIndex = originalFilename.lastIndexOf(DOT);

        if (lastIndex < 0) {
            throw new FileException(FILE_FORMAT_ERROR);
        }
        String fileBaseName = UUID.randomUUID().toString().substring(0, 8);
        String extension = originalFilename.substring(lastIndex + 1);
        validateFileName(fileBaseName);
        validateExtension(extension);

        return fileBaseName + UNDER_BAR + System.currentTimeMillis() + DOT + extension;
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FileException(FILE_BASE_NAME_ERROR);
        }
    }

    private void validateExtension(String extension) {
        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileException(FILE_EXTENSION_ERROR);
        }
    }

    private void transferFile(MultipartFile file, File uploadPath) {
        try {
            file.transferTo(uploadPath);
        } catch (IOException e) {
            throw new FileException(FILE_TRANSFER_ERROR, e);
        }
    }

    private String getImageFullPath(String imageLocalPath) {
        return url + SLASH + imageLocalPath;
    }
}
