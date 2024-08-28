package com.ericsson.taf.selenium.node.sikuli.rmi;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.sikuli.api.ImageTarget;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Collections2.filter;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.listFilesAndDirs;
import static org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY;
import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.10.06.
 */
public class TargetFactory {

    private File folderToScan = new File(".");

    public void setImagePrefix(String imagePrefix) {
        String imageFolder = !imagePrefix.endsWith("/") ? imagePrefix + "/" : imagePrefix;
        this.folderToScan = new File(imageFolder);
        checkState(folderToScan.exists(), "Folder %s does not exist", folderToScan.getAbsolutePath());
        checkState(folderToScan.isDirectory(), "Folder %s is not a folder", folderToScan.getAbsolutePath());
    }

    public ImageTarget createImageTarget(String imageFile) {
        return new ImageTarget(findImageFile(imageFile));
    }

    @VisibleForTesting
    protected File findImageFile(String imageFile) {
        File foundImageFile = findImageFile(folderToScan, imageFile);

        // creating non existing file to avoid NPE
        return foundImageFile == null ? new File(imageFile) : foundImageFile;
    }

    private File findImageFile(File inFolder, String file) {

        // checkstyle requirement
        File folderToScan = inFolder;
        String fileToFind = file;

        // given file can contain sub-folders in its name
        String[] paths = fileToFind.split("[/\\\\]");
        String[] foldersToFind = Arrays.copyOfRange(paths, 0, paths.length - 1);
        for (String folderToFind : foldersToFind) {
            folderToScan = findFolderRecursively(folderToScan, folderToFind);
            if (folderToScan == null) {
                return null;
            }
        }
        fileToFind = paths[paths.length - 1];

        // finally searching for file name recursively
        Collection<File> files = listFiles(folderToScan, new NameFileFilter(fileToFind), TRUE);
        return files.isEmpty() ? null : files.iterator().next();
    }

    private File findFolderRecursively(File folderToScan, final String folderToFind) {

        // filtering out empty arguments
        if (folderToFind.isEmpty()) {
            return folderToScan;
        }

        // collecting all the sub-folders recursively
        Collection<File> foundFolders = listFilesAndDirs(folderToScan, DIRECTORY, TRUE);

        // filtering all the folders by folder name
        foundFolders = filter(foundFolders, new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                String fileName = file.getAbsolutePath();
                return fileName.endsWith("/" + folderToFind) || fileName.endsWith("\\" + folderToFind);
            }
        });
        return foundFolders.isEmpty() ? null : foundFolders.iterator().next();
    }

}
