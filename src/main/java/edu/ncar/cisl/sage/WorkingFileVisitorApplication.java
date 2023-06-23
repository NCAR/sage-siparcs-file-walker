package edu.ncar.cisl.sage;

import edu.ncar.cisl.sage.filewalker.FileWalker;
import edu.ncar.cisl.sage.filewalker.LoggingFileVisitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

@SpringBootApplication
@Configuration
public class WorkingFileVisitorApplication {

    @Value("${walker.startingPath}")
    private String startingPath;

//	Create Interface for FileVisitor so that multiple can be made and passed in to be used??
//	Needs to be updated so that it supports FileVisitor<Path> throughout the program ??
//	@Value("${walker.fileVisitor})
//	private FileVisitor<Path> fileVisitor

    @Value("${config.ignoredPaths}")
    private List<String> ignoredPaths;

    public static void main(String[] args) {
        SpringApplication.run(WorkingFileVisitorApplication.class, args);
    }

    @Bean
    public FileWalker fileWalker() {

        System.out.println("Starting Path: " + this.startingPath);
        System.out.println("Ignored Paths: " + this.ignoredPaths);

        LoggingFileVisitor fileVisitor = new LoggingFileVisitor(ignoredPaths);

        return new FileWalker(Path.of(startingPath), fileVisitor, Clock.systemDefaultZone());
    }
}
