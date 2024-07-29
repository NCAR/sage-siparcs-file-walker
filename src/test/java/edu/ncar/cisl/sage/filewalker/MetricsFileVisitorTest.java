package edu.ncar.cisl.sage.filewalker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MetricsFileVisitorTest {

    private MetricsFileVisitor metricsFileVisitorContinue;
    private MetricsFileVisitor metricsFileVisitorSkip;

    // For Mockito
    private MetricsFileVisitor metricsFileVisitor;
    private Visitor visitor;

    @BeforeEach
    public void setup() {

        metricsFileVisitorContinue = new MetricsFileVisitor(new Visitor(FileVisitResult.CONTINUE));
        metricsFileVisitorSkip = new MetricsFileVisitor(new Visitor(FileVisitResult.SKIP_SUBTREE));

        visitor = mock(Visitor.class);
        metricsFileVisitor = new MetricsFileVisitor(visitor);
    }

    @Test
    public void give_visitor_return_value__when_pre_visit__then_return_same_value() throws IOException {

        Assertions.assertEquals(FileVisitResult.CONTINUE, metricsFileVisitorContinue.preVisitDirectory(null, null));
        Assertions.assertEquals(FileVisitResult.SKIP_SUBTREE, metricsFileVisitorSkip.preVisitDirectory(null, null));
    }

    @Test
    public void give_visitor_return_value_and_regular_file_path__when_visit__then_countFile() throws IOException {

        Path path = Path.of("src/test/resources/TestFile");

        Assertions.assertEquals(0, getCountFileWhenVisitFile(metricsFileVisitorSkip, path));
        Assertions.assertEquals(1, getCountFileWhenVisitFile(metricsFileVisitorContinue, path));
    }

    @Test
    public void give_not_regular_file_path__when_visit_then_countFile_equals_zero() throws IOException {

        Path directoryPath = Path.of("src/test/resources");
        Assertions.assertEquals(0, getCountFileWhenVisitFile(metricsFileVisitorContinue, directoryPath));
    }

    @Test
    public void give_visitor_return_value_and_regular_directory_path__when_post_visit__then_countDirectory() throws IOException {

        Path path = Path.of("src/test/resources");

        Assertions.assertEquals(0, getCountDirectoryPostVisit(metricsFileVisitorSkip, path));
        Assertions.assertEquals(1, getCountDirectoryPostVisit(metricsFileVisitorContinue, path));
    }

    @Test
    public void give_not_regular_directory_path__when_visit_then_countDirectory_equals_zero() throws IOException {

        Path path = Path.of("src/test/resources/TestFile");
        Assertions.assertEquals(0, getCountDirectoryPostVisit(metricsFileVisitorContinue, path));
    }

    private long getCountFileWhenVisitFile(MetricsFileVisitor visitor, Path path) throws IOException {

        BasicFileAttributes attrs = Files. readAttributes(path, BasicFileAttributes. class);
        visitor.visitFile(path, attrs);
        return visitor.getCountFile();
    }

    private long getCountDirectoryPostVisit(MetricsFileVisitor visitor, Path path) throws IOException {

        visitor.postVisitDirectory(path, new IOException());
        return visitor.getCountDirectory();
    }

    private static class Visitor implements FileVisitor<Path> {

        FileVisitResult visitResult;

        public Visitor(FileVisitResult visitResult) {

            this.visitResult = visitResult;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

            return visitResult;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

            return visitResult;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return null;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            return visitResult;
        }
    }

    @Test
    public void given_path__when_visit_file_failed__then_countError() {

        Assertions.assertEquals(1, getErrorCount(false, true, "file"));
        Assertions.assertEquals(1, getErrorCount(true, false, "directory"));
        Assertions.assertEquals(1, getErrorCount(false, false, "other"));
    }

    public long getErrorCount(boolean isDirectory, boolean isRegularFile, String errorType) {

        Path path = Path.of("some path");
        IOException e = new IOException("error");

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.isDirectory(path)).thenReturn(isDirectory);
            mockedFiles.when(() -> Files.isRegularFile(path)).thenReturn(isRegularFile);

            metricsFileVisitor.visitFileFailed(path, e);

            switch (errorType) {
                case "file":
                    return metricsFileVisitor.getCountErrorFile();

                case "directory":
                    return metricsFileVisitor.getCountErrorDirectory();

                case "other":
                    return metricsFileVisitor.getCountErrorOther();
            }

            verify(visitor).visitFileFailed(path, e);

        } catch (IOException exception) {

            throw new RuntimeException(exception);
        }

        return -1;
    }
}
