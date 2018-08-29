import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class hubXmlPostFinder {

    private static List<String> postsToScrub = new ArrayList<String>();

    private static void paginateAndGrabPosts(String listingUri){

        try {
            org.jsoup.nodes.Document listing = Jsoup.connect(listingUri).get();
            Elements listingLinks = listing.select(hubXmlSelectors.BLOG_LISTING_LINKS_SELECTOR);
            if (!listingLinks.isEmpty()) {
                for (org.jsoup.nodes.Element listingLink : listingLinks) {
                    if (listingLink.toString().contains("http")) {
                        postsToScrub.add(listingLink.attr("href"));
                    } else {
                        postsToScrub.add(hubXmlSelectors.BLOG_LISTING_URL.split("(?<!/)/(?!/)")[0] + listingLink.attr("href"));
                    }
                }
            }
            Elements nextPage = listing.select(hubXmlSelectors.BLOG_LISTING_PAGINATOR);
            if (!nextPage.isEmpty()) {
                if (nextPage.toString().contains("http")) {
                    paginateAndGrabPosts(nextPage.attr("href"));
                } else {
                    paginateAndGrabPosts(hubXmlSelectors.BLOG_LISTING_URL.split("(?<!/)/(?!/)")[0] + nextPage.attr("href"));
                }
            }

        } catch (Exception e) {

            System.out.println("Listing page link scrubbing borked :(");

        }
    }

    static List<String> findBlogPosts() {

        String sitemapUri = hubXmlSelectors.BLOG_ROOT_URL.split("(?<!/)/(?!/)")[0] + "/sitemap.xml";

        if (hubXmlSelectors.BLOG_ROOT_URL.length() != 0) {

            try {

                String sitemapXml = Jsoup.connect(sitemapUri).userAgent(hubXmlBuilders.USER_AGENT).get().toString();
                org.jsoup.nodes.Document sitemapXmlParser = Jsoup.parse(sitemapXml, "", Parser.xmlParser());
                for (org.jsoup.nodes.Element e : sitemapXmlParser.select("loc")) {
                    if (e.text().contains(hubXmlSelectors.BLOG_ROOT_URL) &&! e.text().equals(hubXmlSelectors.BLOG_ROOT_URL) &&! e.text().toLowerCase().matches("^.*?(author|tag|topic|category).*$")) {
                        postsToScrub.add(e.text());
                    }
                }
                System.out.println("Found the following posts from " + sitemapUri + ": " + postsToScrub);

            } catch (Exception e) {

                System.out.println("Failed to find posts from sitemap " + sitemapUri + "Set BLOG_ROOT_URL to be an empty string(\"\") and manually set POSTS array");

            }

        } else if (hubXmlSelectors.BLOG_LISTING_URL.length() != 0) {

            paginateAndGrabPosts(hubXmlSelectors.BLOG_LISTING_URL);

        } else {

            System.out.println("Using posts from POSTS array. Set BLOG_ROOT_URL to grab post urls from sitemap if the blog has a root url pattern");
            postsToScrub = Arrays.asList(hubXmlSelectors.POSTS);

        }

        return postsToScrub;

    }

}
