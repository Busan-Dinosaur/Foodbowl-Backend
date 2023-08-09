package org.dinosaur.foodbowl.domain.photo.application;

import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_BASE_NAME_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_EXTENSION_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_FORMAT_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_TRANSFER_ERROR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileSystemPhotoUtils implements PhotoUtils {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpeg", "jpg", "png", "webp");
    private static final String SLASH = "/";
    private static final String DASH = "-";
    private static final String DOT = ".";

    @Value("${file.dir}")
    private String rootDirectory;

    public List<String> upload(List<MultipartFile> files, String parentDirectory) {
        File directory = loadDirectory(rootDirectory + SLASH + parentDirectory);

        List<String> filePaths = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            if (multipartFile == null || multipartFile.isEmpty()) {
                continue;
            }
            String originalFilename = multipartFile.getOriginalFilename();
            validateFileName(originalFilename);
            String saveFileName = convertToPathWithName(originalFilename);

            File uploadPath = new File(directory, saveFileName);
            transferFile(multipartFile, uploadPath);
            filePaths.add(uploadPath.getPath());
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

    private String convertToPathWithName(String originalFilename) {
        int lastIndex = originalFilename.lastIndexOf(DOT);

        if (lastIndex < 0) {
            throw new FileException(FILE_FORMAT_ERROR);
        }
        String fileBaseName = originalFilename.substring(0, lastIndex);
        String extension = originalFilename.substring(lastIndex + 1);
        validateFileName(fileBaseName);
        validateExtension(extension);

        return fileBaseName + DASH + System.currentTimeMillis() + DOT + extension;
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
            throw new FileException(FILE_TRANSFER_ERROR);
        }
    }
}
