import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class hubXmlBuilders {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    // XML Setup
    private static final Namespace CONTENT_ENCODED = Namespace.getNamespace("content", "http://purl.org/rss/1.0/modules/content/");
    private static final Namespace EXCERPT_ENCODED = Namespace.getNamespace("excerpt", "http://wordpress.org/export/1.2/excerpt/");
    private static final Namespace WP = Namespace.getNamespace("wp", "http://wordpress.org/export/1.2/");
    private static final Namespace DC = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
    private static ArrayList<String> authorList = new ArrayList<String>();
    static List<Element> items = new ArrayList<Element>();
    static List<Element> featuredItems = new ArrayList<Element>();
    static Document document = new Document();
    static Element rss = new Element("rss");
    static Element channel = new Element("channel");
    private static Element rootLink = new Element("link");
    private static int commentId = 0;

    static void buildXmlSetup() {

        rss.addNamespaceDeclaration(CONTENT_ENCODED);
        rss.addNamespaceDeclaration(EXCERPT_ENCODED);
        rss.addNamespaceDeclaration(WP);
        rss.addNamespaceDeclaration(DC);
        rss.addContent(channel);
        channel.addContent(rootLink);
        rootLink.setText("https://www.blogblogblog.com");
    }

    private static void buildWpAuthor(String author) {

        authorList.add(author);
        Element wpAuthor = new Element("author", WP);
        channel.addContent(wpAuthor);
        CDATA wpAuthorDisplayNameCdata = new CDATA(author);
        CDATA wpAuthorloginCdata = new CDATA(author);
        Element wpAuthorDisplayName = new Element("author_display_name", WP).addContent(wpAuthorDisplayNameCdata);
        Element wpAuthorlogin = new Element("author_login", WP).addContent(wpAuthorloginCdata);
        wpAuthor.addContent(wpAuthorDisplayName);
        wpAuthor.addContent(wpAuthorlogin);

    }

    private static void buildFeaturedImage(String post, String featuredImageUri, Integer id) {

        Element featuredItem = new Element("item");
        featuredItem.addContent(new Element("title").setText(post));
        featuredItem.addContent(new Element("link").setText(featuredImageUri.split("[?]")[0]));
        featuredItem.addContent(new Element("post_id", WP).setText(String.valueOf(id + 4000)));
        featuredItem.addContent(new Element("post_parent", WP).setText(String.valueOf(id + 1)));
        featuredItem.addContent(new Element("post_type", WP).setText("attachment"));
        featuredItem.addContent(new Element("attachment_url", WP).setText(featuredImageUri.split("[?]")[0]));
        featuredItems.add(featuredItem);

    }

    private static String getPubDate(String fethUri) {

        try {

            URL obj = new URL(fethUri);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseString = response.toString();
            return responseString.split("\"utcDate\":\"")[1].split("\"")[0];

        } catch (Exception e) {

            return "Wed, 25 Apr 2018 13:19:35 +0000";

        }

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
            } else {
                System.out.println("Failed to find the title of " + post +
                        ", so it was set to \"Sample Title\"");
                item.addContent(new Element("title").setText("Sample Title"));
            }

            // Build <link>
            item.addContent(new Element("link").setText(post));

            // Build <pubDate>
            Elements date = doc.select(hubXmlSelectors.DATE_SELECTOR);
            if (!date.isEmpty()) {
                String dateString = date.get(0).text();
                String fetchDate = dateString.replace(","," ").replace("-"," ").replace(" ","%20").replace("/","%2F");
                String finalPubDate =  getPubDate("http://www.convert-unix-time.com/api?format=rfc1123&date=" + fetchDate);
                Element pubDate = new Element("pubDate").setText(finalPubDate);
                item.addContent(pubDate);
            } else {
                System.out.println("Failed to find the publish date of " + post + ", so it was set to Wed, 25 Apr 2018 13:19:35 +0000");
                item.addContent(new Element("pubDate").setText("Wed, 25 Apr 2018 13:19:35 +0000" ));
            }

            // Build <wp:postIid>
            item.addContent(new Element("post_id", WP).setText(String.valueOf(id + 1)));

            // Build <wp:status>
            item.addContent(new Element("status", WP).setText("publish"));

            // Build <wp:post_type>
            item.addContent(new Element("post_type", WP).setText("post"));

            // Build <excerpt:encoded>
            Elements metaD = doc.select(hubXmlSelectors.META_DESCRIPTION_SELECTOR);
            if (!metaD.isEmpty()) {
                Element excerptEncoded = new Element("encoded", EXCERPT_ENCODED);
                CDATA excerptEncodedCdata = new CDATA(metaD.get(0).attr("content"));
                excerptEncoded.setContent(excerptEncodedCdata);
                item.addContent(excerptEncoded);
            } else {
                System.out.println("Failed to find the meta description of " + post + ", so it was set to \"Sample meta description\"");
                item.addContent(new Element("encoded", EXCERPT_ENCODED).setText("Sample meta description" ));
            }

            // Build <dc:creator>
            Elements authorElement = doc.select(hubXmlSelectors.AUTHOR_SELECTOR);
            if (!authorElement.isEmpty()) {
                String author = authorElement.get(0).text();
                Element dcCreator = new Element("creator", DC);
                dcCreator.setText(author);
                item.addContent(dcCreator);
                // Build <wp:author>
                if (!authorList.contains(author)) {
                    buildWpAuthor(author);
                }
            } else if (!authorList.isEmpty()) {
                System.out.println("Failed to find the author of " + post + ", so its author was set to " + authorList.get(0));
                item.addContent(new Element("creator", DC).setText(authorList.get(0)));
            } else {
                System.out.println("Failed to find the author of " + post + "and there is no previously found authors in this blog, so a default author \"Admin\" was added");
                item.addContent(new Element("creator", DC).setText("Admin"));
                buildWpAuthor("Admin");
            }

            // Build <category>(s)
            if (hubXmlSelectors.TAGS_SELECTOR.length() != 0) {
                Elements tags = doc.select(hubXmlSelectors.TAGS_SELECTOR);
                if (!tags.isEmpty()) {
                    for (org.jsoup.nodes.Element tag : tags) {
                        Element category = new Element("category").setText(tag.ownText());
                        category.setAttribute("domain", "category");
                        category.setAttribute("nicename", tag.ownText().replace(" ", "-"));
                        item.addContent(category);
                    }
                }
            }

            // Build <content:encoded>
            Elements postBody = doc.select(hubXmlSelectors.POST_BODY_SELECTOR);
            for (String remover : hubXmlSelectors.POST_BODY_SELECTOR_REMOVER) {
                postBody.select(remover).remove();
            }
            if (!postBody.isEmpty()) {
                Element contentEncoded = new Element("encoded", CONTENT_ENCODED);
                CDATA contentEncodedCdata = new CDATA(postBody.get(0).toString());
                contentEncoded.setContent(contentEncodedCdata);
                item.addContent(contentEncoded);
            } else {
                System.out.println("Failed to find the post body of " + post + ", so it was set to \"Sample post body\"");
                item.addContent(new Element("encoded", CONTENT_ENCODED).setText("Sample post body"));
            }

            // Build <wp:postmeta> for featured image
            Elements featuredImage = doc.select(hubXmlSelectors.FEATURED_IMAGE_SELECTOR);
            if (!featuredImage.isEmpty()) {

                // IF IMAGE SRC IS SRC ELEMENT OF IMG
                String featuredImageUri = featuredImage.get(0).attr("src");

                // IF IMAGE SRC IS CONTENT ATTRIBUTE OF META TAG (SELECTOR = meta[property=og:image])
                // String featuredImageUri = featuredImage.get(0).attr("content");

                // IF IMAGE SRC IS IN INLINE CSS OF ELEMENT, USE BELOW INSTEAD. MAKE SURE TO CHECK indexOfs values:
                // PROTOCOL OF IMAGE SRC | indexOf("https://") or indexOf("http://")
                // BACKGROUND URL SYNTAX | indexOf("')") OR indexOf(")")
                // String featuredImageStyle = featuredImage.attr("style");
                // String featuredImageUri =  featuredImageStyle.substring(featuredImageStyle.indexOf("https://"), featuredImageStyle.indexOf("')"));

                Element postMeta = new Element ("post_meta", WP);
                item.addContent(postMeta);
                postMeta.addContent(new Element ("meta_key", WP).setText("_thumbnail_id"));
                Element metaValue = new Element ("meta_value", WP);
                CDATA metaValueCdata = new CDATA(String.valueOf(id + 4000));
                metaValue.addContent(metaValueCdata);
                postMeta.addContent(metaValue);
                // Build <item> for featured image
                buildFeaturedImage(post, featuredImageUri, id);
            }

            // Build <wp:comment>(s)
            Elements comments = doc.select(hubXmlSelectors.COMMENT_WRAPPER_SELECTOR);
            if (!comments.isEmpty()) {
                for (org.jsoup.nodes.Element comment : comments) {
                    Element wpComment = new Element("comment", WP);
                    wpComment.addContent(new Element("comment_id", WP).setText(String.valueOf(commentId)));
                    if (!comment.select(hubXmlSelectors.COMMENT_AUTHOR_SELECTOR).isEmpty()) {
                        wpComment.addContent(new Element("comment_author", WP).setText(comment.select(hubXmlSelectors.COMMENT_AUTHOR_SELECTOR).get(0).text()));
                    } else {
                        wpComment.addContent(new Element("comment_author", WP).setText("Anonymous Commenter"));
                        System.out.println("Failed to find a comment author name, so it was set to \"Anonymous Commenter\"");
                    }
                    if (!comment.select(hubXmlSelectors.COMMENT_AUTHOR_EMAIL_SELECTOR).isEmpty()) {
                        wpComment.addContent(new Element("comment_author_email", WP).setText(comment.select(hubXmlSelectors.COMMENT_AUTHOR_EMAIL_SELECTOR).get(0).text()));
                    } else {
                        wpComment.addContent(new Element("comment_author_email", WP).setText("AnonymousCommenter@AnonymousCommenter.com"));
                        System.out.println("Failed to find a comment author email, so it was set to \"AnonymousCommenter@AnonymousCommenter.com\"");
                    }
                    // TODO figure out how to deal with comment date
                    wpComment.addContent(new Element("comment_date", WP).setText("2018-07-02 17:49:32"));
                    Element commentContent = new Element("comment_content", WP);
                    CDATA commentContentCdata = new CDATA(comment.select(hubXmlSelectors.COMMENT_TEXT_SELECTOR).get(0).text());
                    commentContent.addContent(commentContentCdata);
                    wpComment.addContent(commentContent);
                    wpComment.addContent(new Element("comment_approved", WP).setText("1"));
                    item.addContent(wpComment);
                    commentId++;
                }
            }

            // Add Built <item> to list items
            items.add(item);

        }  catch (Exception e) {

            System.out.println("Failed to find post " + post);
            // e.printStackTrace();

        }

    }

}
