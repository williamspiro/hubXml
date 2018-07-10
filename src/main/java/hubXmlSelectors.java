class hubXmlSelectors {

    // :)
    // VARIABLES TO SET
    // :)
    // Required Elements for a given <item> ff an element is not found in a given post, a default value is set, and the error is logged
     static final String[] POSTS = {"https://www.kungfupanda.com/dragon-warrior", "https://www.kungfupanda.com/valley-of-peace", "https://www.kungfupanda.com/jade-palace"};
    static final String TITLE_SELECTOR = "title";
    static final String DATE_SELECTOR = ".published";
    static final String META_DESCRIPTION_SELECTOR = "meta[name=description]";
    static final String AUTHOR_SELECTOR = "a[rel=author]";
    static final String TAGS_SELECTOR = "a[rel=category tag]";
    static final String POST_BODY_SELECTOR = ".post-body";

    // Optional elements for a given <item> no default value or error is logged

    static final String FEATURED_IMAGE_SELECTOR = ".featured-image";
    static final String COMMENT_WRAPPER_SELECTOR = ".comment";
    // Comments are optional for a given <item> but within a comment, all comment elements are required
    // If a comment element is not found in a given comment, a default value is set, and the error is logged
    static final String COMMENT_TEXT_SELECTOR = ".comment-content";
    static final String COMMENT_AUTHOR_SELECTOR = ".name";
    static final String COMMENT_AUTHOR_EMAIL_SELECTOR = ".email";
    // :)
    // END VARIABLES TO SET
    // :)

}
