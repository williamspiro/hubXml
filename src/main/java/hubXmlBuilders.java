import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

class hubXmlBuilders {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    // XML Setup
    private static Namespace ce = Namespace.getNamespace("content", "http://purl.org/rss/1.0/modules/content/");
    private static Namespace ee = Namespace.getNamespace("excerpt", "http://wordpress.org/export/1.2/excerpt/");
    private static Namespace wp = Namespace.getNamespace("wp", "http://wordpress.org/export/1.2/");
    private static Namespace dc = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
    private static ArrayList<String> authorList = new ArrayList<String>();
    static List items = new ArrayList();
    static List featuredItems = new ArrayList();
    static Document document = new Document();
    static Element rss = new Element("rss");
    static Element channel = new Element("channel");
    private static Element rootLink = new Element("link");

    static void buildXmlSetup() {

        rss.addNamespaceDeclaration(ce);
        rss.addNamespaceDeclaration(ee);
        rss.addNamespaceDeclaration(wp);
        rss.addNamespaceDeclaration(dc);
        rss.addContent(channel);
        channel.addContent(rootLink);
        rootLink.setText(hubXmlSelectors.ROOT_URL);
    }

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

    private static void buildFeaturedImage(String post, String featuredImageUri, Integer id) {

        Element featuredItem = new Element("item");
        featuredItem.addContent(new Element("title").setText(post));
        featuredItem.addContent(new Element("link").setText(featuredImageUri.split("[?]")[0]));
        featuredItem.addContent(new Element("post_id", wp).setText(String.valueOf(id + 4000)));
        featuredItem.addContent(new Element("post_parent", wp).setText(String.valueOf(id + 1)));
        featuredItem.addContent(new Element("post_type", wp).setText("attachment"));
        featuredItem.addContent(new Element("attachment_url", wp).setText(featuredImageUri.split("[?]")[0]));
        featuredItems.add(featuredItem);

    }

    static void buildItem(String post, Integer id) {

        try {

            // Fetch the page
            org.jsoup.nodes.Document doc = Jsoup.connect(post).userAgent(USER_AGENT).get();

            // Build <item>
            Element item = new Element("item");

            // Build <title>
            Elements title = doc.select(hubXmlSelectors.TITLE_SELECTOR);
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
            Elements metaD = doc.select(hubXmlSelectors.META_DESCRIPTION_SELECTOR);
            if (!metaD.isEmpty()) {
                Element excerptEncoded = new Element("encoded", ee);
                CDATA excerptEncodedCdata = new CDATA(metaD.get(0).attr("content"));
                excerptEncoded.setContent(excerptEncodedCdata);
                item.addContent(excerptEncoded);
            }

            // Build <dc:creator>
            Elements authorElement = doc.select(hubXmlSelectors.AUTHOR_SELECTOR);
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
            Elements tags = doc.select(hubXmlSelectors.TAGS_SELECTOR);
            if (!tags.isEmpty()) {
                for (org.jsoup.nodes.Element tag : tags) {
                    Element category = new Element("category").setText(tag.ownText());
                    category.setAttribute("domain", "category");
                    category.setAttribute("nicename", tag.ownText().replace(" ", "-"));
                    item.addContent(category);
                }
            }

            // Build <content:encoded>
            Elements postBody = doc.select(hubXmlSelectors.POST_BODY_SELECTOR);
            if (!postBody.isEmpty()) {
                Element contentEncoded = new Element("encoded", ce);
                CDATA contentEncodedCdata = new CDATA(postBody.get(0).toString());
                contentEncoded.setContent(contentEncodedCdata);
                item.addContent(contentEncoded);
            }

            // Build <wp:postmeta> for featured image
            Elements featuredImage = doc.select(hubXmlSelectors.FEATURED_IMAGE_SELECTOR);
            if (!featuredImage.isEmpty()) {

                String featuredImageUri = featuredImage.get(0).attr("src");
                // IF IMAGE SRC IS IN INLINE CSS OF ELEMENT, USE BELOW INSTEAD. MAKE SURE TO CHECK indexOfs values:
                // PROTOCOL OF IMAGE SRC | indexOf("https://") or indexOf("http://")
                // BACKGROUND URL SYNTAX | indexOf("')") OR indexOf(")") OR indexOf("')")
                // String featuredImageStyle = featuredImage.attr("style");
                // String featuredImageUri =  featuredImageStyle.substring(featuredImageStyle.indexOf("https://"), featuredImageStyle.indexOf("')"));

                Element postMeta = new Element ("post_meta", wp);
                item.addContent(postMeta);
                postMeta.addContent(new Element ("meta_key", wp).setText("_thumbnail_id"));
                Element metaValue = new Element ("meta_value", wp);
                CDATA metaValueCdata = new CDATA(String.valueOf(id + 4000));
                metaValue.addContent(metaValueCdata);
                postMeta.addContent(metaValue);
                // Build <item> for featured image
                buildFeaturedImage(post, featuredImageUri, id);
            }

            // Add Built <item> to list items
            items.add(item);

        }  catch (Exception e) {

            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();

        }

    }

}
