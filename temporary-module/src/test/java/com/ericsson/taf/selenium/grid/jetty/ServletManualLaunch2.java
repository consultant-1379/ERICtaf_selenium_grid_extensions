package com.ericsson.taf.selenium.grid.jetty;

import com.ericsson.taf.selenium.node.upload.FileUploadServlet;
import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.servlet.ServletContextHandler;
import org.seleniumhq.jetty9.servlet.ServletHolder;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 22/09/2015
 *         <p/>
 */
public class ServletManualLaunch2 {
    public static void main(String[] args) throws Exception {


        Server server = new Server(5555);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new FileUploadServlet()), "/extra/FileUploadServlet/*");

        server.start();
        server.join();
    }
}
