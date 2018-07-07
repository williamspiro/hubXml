import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;

// :)
// SELECTORS ARE SET IN hubXmlSelectors.java
// XML BUILDING HAPPENS IN hubXmlBuilders.java
// :)

public class hubXml {

    public static void main(String[] args) {

        // Build XML Setup
        hubXmlBuilders.buildXmlSetup();

        for(int i=0; i< hubXmlSelectors.POSTS.length; i++) {

            // Build <items>
            hubXmlBuilders.buildItem(hubXmlSelectors.POSTS[i], i);

        }

        // Finally add <item>(s) to <channel> to ensure <wp:authors> are on top
        hubXmlBuilders.channel.addContent(hubXmlBuilders.featuredItems);
        hubXmlBuilders.channel.addContent(hubXmlBuilders.items);
        hubXmlBuilders.document.setContent(hubXmlBuilders.rss);

        try {

            // Build blog.xml and log output
            FileWriter writer = new FileWriter("blog.xml");
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(hubXmlBuilders.document, writer);
            outputter.output(hubXmlBuilders.document, System.out);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}