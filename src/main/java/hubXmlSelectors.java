class hubXmlSelectors {

    // :)
    // VARIABLES TO SET
    // :)

    // There are 3 options for finding posts. Sitemap, listing pagination, and manual listing (order of how easy)
    // For the options you are not using, leave the selectors as empty strings ""
    // For full requirements of the 3 options, see README]
    //  1. SITEMAP - finds posts from sitemaps based on a blog root URL and sitemap
    static final String BLOG_ROOT_URL = "https://www.kungfupanda.com/blog";

    // 2. LISTING PAGINATION - jumps through paginated listing pages grabbing post links along the way
    static final String BLOG_LISTING_URL = "https://www.kungfupanda.com/blog";
    static final String BLOG_LISTING_LINKS_SELECTOR = ".post-link a";
    static final String BLOG_LISTING_PAGINATOR = ".next";

    // 3. MANUAL LISTING - a manual array of posts to run through hubXml
    static final String[] POSTS = {"https://www.kungfupanda.com/blog/dragon-warrior", "https://www.kungfupanda.com/blog/valley-of-peace", "https://www.kungfupanda.com/blog/jade-palace"};
    // ONLY USE 1 OF THE 3 ABOVE OPTIONS FOR TELLING HUBXML WHICH POSTS TO SCRUB. SEE README FOR FURTHER DETAILS


    // Required Elements for a given <item> if an element is not found in a given post, a default value is set, and the error is logged
    // NEVER LEAVE A SELECTOR BLANK/EMPTY - JUST THROW IN SOMETHING RANDOM LIKE ".meep" if an element does not exist on a blog
    static final String TITLE_SELECTOR = "h1";
    static final String DATE_SELECTOR = ".date";
    static final String META_DESCRIPTION_SELECTOR = "meta[name=description]";
    static final String AUTHOR_SELECTOR = ".author-link a";
    static final String TAGS_SELECTOR = ".tags-link a";
    static final String POST_BODY_SELECTOR = ".post-body";
    // Optional Element to remove from the post body content (good to use when there is not a post body wrapper)
    static final String[] POST_BODY_SELECTOR_REMOVER = {".social-sharing",".post-body-cta"};

    // Optional elements for a given <item> no default value or error is logged
    static final String FEATURED_IMAGE_SELECTOR = ".featured-image img";



    // Comments are optional for a given <item> but within a comment, all comment elements are required
    // If a comment element is not found in a given comment, a default value is set, and the error is logged
    static final String COMMENT_WRAPPER_SELECTOR = ".comment meep";
    static final String COMMENT_TEXT_SELECTOR = ".comment-content meep";
    static final String COMMENT_AUTHOR_SELECTOR = ".name meep";
    static final String COMMENT_AUTHOR_EMAIL_SELECTOR = ".email meep";
    // :)
    // END VARIABLES TO SET
    // :)

}