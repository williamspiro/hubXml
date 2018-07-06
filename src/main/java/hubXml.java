import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

// :)
// VARIABLES ARE SET IN hubXmlVariables.java
// :)

public class hubXml {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    // XML Setup
    private static Namespace ce = Namespace.getNamespace("content", "http://purl.org/rss/1.0/modules/content/");
    private static Namespace ee = Namespace.getNamespace("excerpt", "http://wordpress.org/export/1.2/excerpt/");
    private static Namespace wp = Namespace.getNamespace("wp", "http://wordpress.org/export/1.2/");
    private static Namespace dc = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
    private static ArrayList<String> authorList = new ArrayList<String>();
    private static List items = new ArrayList();
    private static Document document = new Document();
    private static Element rss = new Element("rss");
    private static Element channel = new Element("channel");
    private static Element rootLink = new Element("link");

    private static void buildWpAuthor(String author) {

        authorList.add(author);
        Element wpAuthor = new Element("author", wp);
        channel.addContent(wpAuthor);
        CDATA wpAuthorDisplayNameCdata = new CDATA(author);
        CDATA wpAuthorloginCdata = new CDATA(author);
        Element wpAuthorDisplayName = new Element("author_display_name", wp).addContent(wpAuthorDisplayNameCdata);
        Element wpAuthorlogin = new Element("author_login", wp).addContent(wpAuthorloginCdata);
        wpAuthor.addContent(wpAuthorDisplayName);
        wpAuthor.addContent(wpAuthorlogin);

    }

    private static void buildItem(String post, Integer id) {

        try {

            // Fetch the page
            org.jsoup.nodes.Document doc = Jsoup.connect(post).userAgent(USER_AGENT).get();

            // Build <item>
            Element item = new Element("item");

            // Build <title>
            Elements title = doc.select(hubXmlVariables.TITLE_SELECTOR);
            if (!title.isEmpty()) {
                item.addContent(new Element("title").setText(title.get(0).text()));
            }

            // Build <link>
            item.addContent(new Element("link").setText(post));

            // Build <pubDate>
            item.addContent(new Element("pubDate").setText("Wed, 25 Apr 2018 13:19:35 +0000"));

            // Build <wp:postIid>
            Element wpPostId = new Element("post_id", wp);
            wpPostId.setText(String.valueOf(id + 1));
            wpPostId.removeAttribute("wp");
            item.addContent(wpPostId);

            // Build <wp:status>
            Element wpStatus = new Element("status", wp);
            wpStatus.setText("publish");
            item.addContent(wpStatus);

            // Build <wp:post_type>
            Element wpPostType = new Element("post_type", wp);
            wpPostType.setText("post");
            item.addContent(wpPostType);

            // Build <excerpt:encoded>
            Elements metaD = doc.select(hubXmlVariables.META_DESCRIPTION_SELECTOR);
            if (!metaD.isEmpty()) {
                Element excerptEncoded = new Element("encoded", ee);
                CDATA excerptEncodedCdata = new CDATA(metaD.get(0).attr("content"));
                excerptEncoded.setContent(excerptEncodedCdata);
                item.addContent(excerptEncoded);
            }

            // Build <dc:creator>
            Elements authorElement = doc.select(hubXmlVariables.AUTHOR_SELECTOR);
            if (!authorElement.isEmpty()) {
                String author = authorElement.get(0).text();
                Element dcCreator = new Element("creator", dc);
                dcCreator.setText(author);
                item.addContent(dcCreator);
                // Build <wp:author>
                if (!authorList.contains(author)) {
                    buildWpAuthor(author);
                }
            }

            // Build <category>(s)
            Elements tags = doc.select(hubXmlVariables.TAGS_SELECTOR);
            if (!tags.isEmpty()) {
                for (org.jsoup.nodes.Element tag : tags) {
                    Element category = new Element("category").setText(tag.ownText());
                    category.setAttribute("domain", "category");
                    category.setAttribute("nicename", tag.ownText().replace(" ", "-"));
                    item.addContent(category);
                }
            }

            // Build <content:encoded>
            Elements postBody = doc.select(hubXmlVariables.POST_BODY_SELECTOR);
            if (!postBody.isEmpty()) {
                Element contentEncoded = new Element("encoded", ce);
                CDATA contentEncodedCdata = new CDATA(postBody.get(0).toString());
                contentEncoded.setContent(contentEncodedCdata);
                item.addContent(contentEncoded);
            }

            // Add Built <item> to list items
            items.add(item);

        }  catch(Exception e) {

            System.out.println("ERROR I just spent a day at the beach mate... " + e.getMessage());
            e.printStackTrace();

        }

    }

    public static void main(String[] args) {

        rss.addNamespaceDeclaration(ce);
        rss.addNamespaceDeclaration(ee);
        rss.addNamespaceDeclaration(wp);
        rss.addNamespaceDeclaration(dc);
        rss.addContent(channel);
        channel.addContent(rootLink);
        rootLink.setText(hubXmlVariables.ROOT_URL);

        for(int i=0; i< hubXmlVariables.POSTS.length; i++) {

            buildItem(hubXmlVariables.POSTS[i], i);

        }

        // Finally add <item>(s) to <channel> to ensure <wp:authors> are on top
        channel.addContent(items);
        document.setContent(rss);

        try {

            FileWriter writer = new FileWriter("blog.xml");
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(document, writer);
            outputter.output(document, System.out);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}

// DESIRED XML OUTPUT
//<?xml version='1.0' encoding='UTF-8'?>
//<rss>
//  <channel>
//    <link>https://www.awesomeblog.com</link>
//    <wp:author>
//      <wp:author_display_name><![CDATA[author]]></wp:author_display_name>
//      <wp:author_login><![CDATA[author]]></wp:author_login>
//    </wp:author>
//    <item>
//      <title>Post Title</title>
//      <pubDate>Wed, 25 Apr 2018 13:19:35 +0000</pubDate>
//      <link>https://www.awesomeblog.com/awesome-post</link>
//      <wp:post_id>1</wp:post_id>
//      <wp:status>publish</wp:status>
//      <wp:post_type>post</wp:post_type>
//      <dc:creator>Author</dc:creator>
//      <category domain="category" nicename="This-is-a-tag"><![CDATA[This is a tag]]></category>
//      <excerpt:encoded><![CDATA[This is the meta description of my awesome post!]]></excerpt:encoded>
//      <content:encoded><![CDATA[<div>This is the post body</div>]]></content:encoded>
//    </item>
//    ...
//  </channel>
//</rss>