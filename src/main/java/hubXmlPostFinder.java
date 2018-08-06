import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class hubXmlPostFinder {

    static List<String> findBlogPosts() {

        List<String> postsToScrub = new ArrayList<String>();
        String sitemapUri = hubXmlSelectors.BLOG_ROOT_URL.split("(?<!/)/(?!/)")[0] + "/sitemap.xml?123";

        if (hubXmlSelectors.BLOG_ROOT_URL.length() != 0) {

            try {

                String sitemapXml = Jsoup.connect(sitemapUri).userAgent(hubXmlBuilders.USER_AGENT).get().toString();
                org.jsoup.nodes.Document sitemapXmlParser = Jsoup.parse(sitemapXml, "", Parser.xmlParser());
                for (org.jsoup.nodes.Element e : sitemapXmlParser.select("loc")) {
                    if (e.text().startsWith(hubXmlSelectors.BLOG_ROOT_URL) &&! e.text().equals(hubXmlSelectors.BLOG_ROOT_URL) &&! e.text().toLowerCase().matches("^.*?(author|tag|topic).*$")) {
                        System.out.println("tag,topic,author match");
                        postsToScrub.add(e.text());
                    }
                }
                System.out.println("Found the following posts from " + sitemapUri + ": " + postsToScrub);

            } catch (Exception e) {

                System.out.println("Failed to find posts from sitemap " + sitemapUri + "Set BLOG_ROOT_URL to be an empty string(\"\") and manually set POSTS array");

            }

        } else {

            System.out.println("Using posts from POSTS array. Set BLOG_ROOT_URL to grab post urls from sitemap if the blog has a root url pattern");
            postsToScrub = Arrays.asList(hubXmlSelectors.POSTS);

        }

        return postsToScrub;

    }

}
