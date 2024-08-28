package com.ericsson.taf.selenium.grid.jetty;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;
import com.ericsson.taf.selenium.node.sikuli.SikuliExtensionServlet;
import com.ericsson.taf.sikuli.client.SikuliClient;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.servlet.ServletContextHandler;
import org.seleniumhq.jetty9.servlet.ServletHolder;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;

import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 22/09/2015
 *         <p/>
 */
public class ServletManualLaunch {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new SikuliExtensionServlet()), "/*");
//        context.addServlet(new ServletHolder(new HubRequestsProxyingServlet()),"/*");

        server.start();
        server.join();
    }


    @Test
    @Ignore
    public void userCanLoginByUsername() throws MalformedURLException, InterruptedException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities("firefox", "", Platform.ANY);
        desiredCapabilities.setCapability("sikuliCapability", true);
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), desiredCapabilities);
        WebDriverRunner.setWebDriver(remoteWebDriver);

        GooglePage page = open("https://www.google.com", GooglePage.class);
        SearchResultsPage results = page.searchFor("selenide");
        results.getResults().get(0).shouldHave(text("Selenide: concise UI tests in Java"));


        SikuliClient sikuliClient = new SikuliClient("localhost", 4444, remoteWebDriver.getSessionId().toString());

        DesktopScreenRegion desktop = sikuliClient.getDesktop();
        ScreenLocation center = desktop.getCenter();

        sikuliClient.getMouse().click(center);
        sikuliClient.uploadResourceBundle("automation");

        Thread.sleep(2000);

        ImageTarget imageTarget = sikuliClient.getTargetFactory().createImageTarget("mspaint-brush.png");
        ScreenRegion brush = sikuliClient.getDesktop().find(imageTarget);
        ScreenLocation center1 = brush.getCenter();
        sikuliClient.getMouse().click(center1);


        close();
    }

    public static class GooglePage {
        public SearchResultsPage searchFor(String text) {
            $(By.name("q")).val(text).pressEnter();
            return page(SearchResultsPage.class);
        }
    }

    public static class SearchResultsPage {
        public ElementsCollection getResults() {
            return $$("#ires .g");
        }
    }
}
