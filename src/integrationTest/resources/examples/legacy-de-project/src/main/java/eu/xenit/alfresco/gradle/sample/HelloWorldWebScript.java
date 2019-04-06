package eu.xenit.alfresco.gradle.sample;

import java.io.IOException;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;

@Component
@WebScript(baseUri = "/eu/xenit")
public class HelloWorldWebScript {
    @Uri(value = "/helloworld", method = HttpMethod.GET)
    public void execute(WebScriptResponse res) throws IOException {
        res.getWriter().write("<h1>Hello World</h1>");
    }
}
