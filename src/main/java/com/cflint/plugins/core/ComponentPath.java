package com.cflint.plugins.core;

import org.apache.tools.ant.taskdefs.Java;

import java.awt.*;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;

public class ComponentPath {
    private static HashMap<String, HashSet<String>> componentsMapCache;
    private Path rootPath;
    private static ComponentPath single_instance = null;
    private String separator;
    private static HashSet<String> CFknownScriptFunctions;

    public static ComponentPath getInstance(String folder) {
        if (single_instance == null)
            single_instance = new ComponentPath(folder);

        return single_instance;
    }

    private ComponentPath(String folderPath) {
        //work around to find web root
        // initialize known sub folders of web root
        //without this, to find the web root , we have to search for server/web.xml coldfusion configuration file
        String[] knownRootSubDirs = {"api", "voucherengine", "components", "avisfuelcard", "commandchain", "ioc", "parrot", "paycard", "santam", "voucherpos", "access", "customTags", "common"};
        HashSet<String> knownRootSubs = new HashSet<>(Arrays.asList(knownRootSubDirs));
        File current = new File(folderPath);
        boolean foundRoot = false;

        do {
            String currentDir = current.getParentFile().getName();
            if (knownRootSubs.contains(currentDir)) {
                rootPath = current.getParentFile().getParentFile().toPath();
                foundRoot = true;
            }
            current = current.getParentFile();
        } while (!foundRoot && current.toPath().getNameCount() != 0);

        componentsMapCache = new HashMap<>();
        separator = System.getProperty("file.separator");

        String[] knownScriptFuncs = {"ftp", "http", "mail", "pdf", "query", "storedproc", "dbinfo", "imap", "pop", "ldap", "feed"};
        CFknownScriptFunctions = new HashSet<>(Arrays.asList(knownScriptFuncs));

    }

    public boolean ComponentExists(String componentName, String currentSrcFile) {

        if (CFknownScriptFunctions.contains(componentName.toLowerCase())) {
            return true;
        }
        if (rootPath == null) {
            System.out.println("root path not found");
        } else {
            Path compPath = getComponentPath(componentName, currentSrcFile);
            HashSet<String> currentDirFiles = initOrGetFileNamesFromCache(compPath);
            if (currentDirFiles.contains(compPath.toAbsolutePath().toString())) {
                return true;
            }
        }
        return false;
    }

    private Path getComponentPath(String componentName, String currentSrcFile) {
        String folderpath = "";
        if (componentName.contains(".")) {
            folderpath = rootPath.toAbsolutePath() + separator + String.join(separator, componentName.split("\\.")) + ".cfc";
        } else {
            Path parentDir = Paths.get(currentSrcFile).getParent();
            folderpath = parentDir.toAbsolutePath().toString() + separator + componentName + ".cfc";
        }
        return Paths.get(folderpath);

    }

    private HashSet<String> initOrGetFileNamesFromCache(Path current) {
        File componentFile = current.toAbsolutePath().toFile();
        if (componentFile.isFile()) {
            current = current.getParent();
        }
        if (!componentsMapCache.containsKey(current)) {
            componentsMapCache.put(current.toString(), new HashSet<>());
        } else {
            return componentsMapCache.get(current.toString());
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(current, "*.{cfc,cfm}")) {
            for (Path entry : stream) {
                componentsMapCache.get(current.toString()).add(entry.toString());
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return componentsMapCache.get(current.toString());
    }
}
