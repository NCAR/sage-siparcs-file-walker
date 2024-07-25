package edu.ncar.cisl.sage.filewalker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class MetricsFileVisitorTest {

    private MetricsFileVisitor metricsFileVisitorContinue;
    private MetricsFileVisitor metricsFileVisitorSkip;

    @BeforeEach
    public void setup() {

        metricsFileVisitorContinue = new MetricsFileVisitor(new Visitor(FileVisitResult.CONTINUE));
        metricsFileVisitorSkip = new MetricsFileVisitor(new Visitor(FileVisitResult.SKIP_SUBTREE));
    }

    @Test
    public void give_visitor_return_value__when_pre_visit__then_return_same_value() throws IOException {

        Assertions.assertEquals(FileVisitResult.CONTINUE, metricsFileVisitorContinue.preVisitDirectory(null, null));
        Assertions.assertEquals(FileVisitResult.SKIP_SUBTREE, metricsFileVisitorSkip.preVisitDirectory(null, null));
    }

//    @Test
//    public void give_visitor_return_continue_and_regular_file_path__when_visit__then_countFile_equals_one() throws IOException {
//
//
//    }

    private static class Visitor implements FileVisitor<Path> {

        FileVisitResult visitResult;

        public Visitor(FileVisitResult visitResult) {

            this.visitResult = visitResult;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

            return visitResult;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

            return visitResult;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return null;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return null;
        }
    }
}
