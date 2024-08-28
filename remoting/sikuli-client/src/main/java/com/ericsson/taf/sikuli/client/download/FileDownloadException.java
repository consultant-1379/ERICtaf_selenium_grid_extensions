package com.ericsson.taf.sikuli.client.download;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 30/09/2015
 *         <p/>
 */
public class FileDownloadException extends RuntimeException {

    public FileDownloadException(String message, int code) {
        super(String.format("Response returned code %d, with message: %s", code, message));
    }

    public FileDownloadException(Throwable cause) {
        super(cause);
    }
}
