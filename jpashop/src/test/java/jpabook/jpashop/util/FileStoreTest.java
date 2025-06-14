package jpabook.jpashop.util;

import jpabook.jpashop.domain.item.UploadFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {FileStore.class})
@TestPropertySource(properties = {
        "file.dir=${user.dir}/src/main/resources/test-images"
})
class FileStoreTest {
    @Autowired
    private FileStore fileStore;

    @AfterEach
    void tearDown() throws IOException {
        Path imagesDir = fileStore.getFileDirPath();
        if (Files.exists(imagesDir)) {
            Files.walk(imagesDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains("-"))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // 무시
                        }
                    });
        }
    }

    @Test
    @DisplayName("단일 파일 저장 성공")
    void storeFile_Success() throws IOException {
        // given
        String filename = "test.jpg";
        String content = "test image content";
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", filename, "image/jpeg", content.getBytes());

        // when
        UploadFile result = fileStore.storeFile(multipartFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUploadFileName()).isEqualTo(filename);
        assertThat(result.getStoreFileName()).isNotEqualTo(filename);
        assertThat(result.getStoreFileName()).endsWith(".jpg");

        // 실제 파일이 저장되었는지 확인
        String fullPath = fileStore.getFullPath(result.getStoreFileName());
        File savedFile = new File(fullPath);
        assertThat(savedFile).exists();
        assertThat(Files.readAllBytes(savedFile.toPath())).isEqualTo(content.getBytes());
    }

    @Test
    @DisplayName("빈 파일 저장시 null 반환")
    void storeFile_EmptyFile_ReturnsNull() throws IOException {
        // given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);

        // when
        UploadFile result = fileStore.storeFile(emptyFile);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("여러 파일 저장 성공")
    void storeFiles_Success() throws IOException {
        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "file1", "test1.png", "image/png", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "file2", "test2.gif", "image/gif", "content2".getBytes());
        List<MultipartFile> files = Arrays.asList(file1, file2);

        // when
        List<UploadFile> results = fileStore.storeFiles(files);

        // then
        assertThat(results).hasSize(2);

        UploadFile result1 = results.get(0);
        assertThat(result1.getUploadFileName()).isEqualTo("test1.png");
        assertThat(result1.getStoreFileName()).endsWith(".png");

        UploadFile result2 = results.get(1);
        assertThat(result2.getUploadFileName()).isEqualTo("test2.gif");
        assertThat(result2.getStoreFileName()).endsWith(".gif");
    }

    @Test
    @DisplayName("빈 파일이 포함된 리스트 저장")
    void storeFiles_WithEmptyFile_SkipsEmptyFiles() throws IOException {
        // given
        MockMultipartFile validFile = new MockMultipartFile(
                "file1", "valid.jpg", "image/jpeg", "content".getBytes());
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file2", "empty.jpg", "image/jpeg", new byte[0]);
        List<MultipartFile> files = Arrays.asList(validFile, emptyFile);

        // when
        List<UploadFile> results = fileStore.storeFiles(files);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUploadFileName()).isEqualTo("valid.jpg");
    }

    @Test
    @DisplayName("저장 파일명은 UUID 형태여야 함")
    void storeFile_GeneratesUUIDFileName() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "original.jpg", "image/jpeg", "content".getBytes());

        // when
        UploadFile result = fileStore.storeFile(file);

        // then
        String storeFileName = result.getStoreFileName();
        String[] parts = storeFileName.split("\\.");

        assertThat(parts).hasSize(2);
        assertThat(parts[1]).isEqualTo("jpg"); // 확장자 확인
        assertThat(parts[0]).matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"); // UUID 패턴
    }

    @Test
    @DisplayName("동일한 파일명으로 저장해도 다른 저장명 생성")
    void storeFile_SameOriginalName_GeneratesDifferentStoreNames() throws IOException {
        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "file1", "same.jpg", "image/jpeg", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "file2", "same.jpg", "image/jpeg", "content2".getBytes());

        // when
        UploadFile result1 = fileStore.storeFile(file1);
        UploadFile result2 = fileStore.storeFile(file2);

        // then
        assertThat(result1.getStoreFileName()).isNotEqualTo(result2.getStoreFileName());
        assertThat(result1.getUploadFileName()).isEqualTo(result2.getUploadFileName());
    }

    @Test
    @DisplayName("다양한 확장자 파일 저장")
    void storeFile_VariousExtensions() throws IOException {
        // given
        String[] extensions = {"jpg", "png", "gif", "bmp", "webp"};

        for (String ext : extensions) {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test." + ext, "image/" + ext, "content".getBytes());

            // when
            UploadFile result = fileStore.storeFile(file);

            // then
            assertThat(result.getStoreFileName()).endsWith("." + ext);
        }
    }
}