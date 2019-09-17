package eu.xenit.alfresco.gradle.sample;

import java.io.IOException;
import java.io.InputStream;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


public class ContentTypeDetectionWebScript extends AbstractWebScript {

    private ContentService contentService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        try {
            NodeRef nodeRef = new NodeRef(req.getParameter("nodeRef"));
            TikaConfig tikaConfig = new TikaConfig();
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, nodeRef.toString());
            try (InputStream content = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT)
                    .getContentInputStream()) {
                MediaType contentType = tikaConfig.getDetector().detect(content, metadata);
                res.getWriter().write("Node has media type " + contentType);
            }
        } catch (TikaException e) {
            throw new RuntimeException(e);
        }
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }
}
