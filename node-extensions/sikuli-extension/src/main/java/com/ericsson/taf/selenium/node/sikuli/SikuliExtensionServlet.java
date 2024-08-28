package com.ericsson.taf.selenium.node.sikuli;

import com.ericsson.taf.rmi.protocol.MethodInvocationDto;
import com.ericsson.taf.rmi.protocol.MethodInvocationResultDto;
import com.ericsson.taf.rmi.protocol.server.RemoteMethodInvocationException;
import com.ericsson.taf.selenium.node.sikuli.rmi.SikuliApplication;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 20/09/2015
 *         <p/>
 */
public class SikuliExtensionServlet extends RegistryBasedServlet {

    private static final Gson GSON = new Gson();

    private static final SikuliApplication SIKULI_APPLICATION = new SikuliApplication();

    public SikuliExtensionServlet() {
        this(null);
    }

    public SikuliExtensionServlet(GridRegistry registry) {
        super(registry);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // getting object ID
        String objectId = getObjectId(req);
        if (objectId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Can't find object ID in URL string");
            return;
        }

        // unmarshalling
        MethodInvocationDto method = GSON.fromJson(req.getReader(), MethodInvocationDto.class);

        try {
            // method invocation
            MethodInvocationResultDto result = SIKULI_APPLICATION.invoke(objectId, method);
            // marshalling
            resp.getWriter().write(GSON.toJson(result));
        } catch (RemoteMethodInvocationException e) {
            resp.getWriter().write(e.getMessage());
            resp.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
    }

    private String getObjectId(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        Pattern pattern = Pattern.compile(".+/([^/]+)");
        Matcher matcher = pattern.matcher(requestURI);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

}
