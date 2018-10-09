package eu.xenit.alfresco.gradle.sample;

import java.io.IOException;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


public class HelloWorldWebScript extends AbstractWebScript {

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        res.getWriter().write("<h1>Hello World</h1>");
    }
}
