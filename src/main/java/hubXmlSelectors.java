class hubXmlSelectors {

    // :)
    // VARIABLES TO SET
    // :)

    // If blog posts have a common path with root url, enter BLOG_ROOT_URL, otherwise, leave it as an empty string ("") and manually set POSTS
    static final String BLOG_ROOT_URL = "https://www.kungfupanda.com/blog";
    static final String[] POSTS = {"https://www.kungfupanda.com/blog/dragon-warrior", "https://www.kungfupanda.com/blog/valley-of-peace", "https://www.kungfupanda.com/blog/jade-palace"};

    // Required Elements for a given <item> if an element is not found in a given post, a default value is set, and the error is logged
    // NEVER LEAVE A SELECTOR BLANK/EMPTY - JUST THROW IN SOMETHING RANDOM LIKE ".meep" if an element does not exist on a blog
    static final String TITLE_SELECTOR = "h1";
    static final String DATE_SELECTOR = ".post-date";
    static final String META_DESCRIPTION_SELECTOR = "meta[name=description]";
    static final String AUTHOR_SELECTOR = ".author-link";
    static final String TAGS_SELECTOR = ".topic-link";
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