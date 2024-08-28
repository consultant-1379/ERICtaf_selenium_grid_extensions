package com.ericsson.taf.sikuli;

import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.servlet.ServletContextHandler;
import org.seleniumhq.jetty9.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 30/09/2015
 *         <p/>
 */
public abstract class BaseRequestTest {
    protected Server startServerForServlet(HttpServlet servlet, String path) throws Exception {
        Server server = new Server(0);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(servlet), path);
        server.start();

        return server;
    }

    public static class StubServlet extends HttpServlet {

        private Function function;

        public void setFunction(Function function) {
            this.function = function;
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            function.apply(req, resp);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            function.apply(req, resp);
        }
    }

    protected interface Function {
        void apply(HttpServletRequest req, HttpServletResponse resp);
    }
}
