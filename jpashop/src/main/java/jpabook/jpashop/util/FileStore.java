package jpabook.jpashop.util;

import jpabook.jpashop.domain.item.UploadFile;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public Path getFileDirPath() {
        return Paths.get(fileDir);
    }

    public String getFullPath(String filename) {
//        return fileDir + filename;
        return Paths.get(fileDir, filename).toString();
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles)
            throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException
    {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 파일 저장 경로
        File destFile = makeDirIfNotExist(storeFileName);

        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        log.info(" 파일 저장 완료: {}", destFile.getAbsolutePath());
        return new UploadFile(originalFilename, storeFileName);
    }

    private File makeDirIfNotExist(String storeFileName) {
        File destFile = new File(getFullPath(storeFileName));
        destFile.getParentFile().mkdirs();
        return destFile;
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}