# hubXml
BOWLINGA tool to turn any blog into a HubSpot importable XML file. It will grab post titles, meta descriptions, publish dates, authors, tags, featured images and post bodies, and turns them all into importable XML `<items>`, building a HubSpot importable XML file. Plain and simple, this allows you to __import any blog into HubSpot__, not just Wordpress blogs :tada:

## hubXml/src/main/java/hubXml.java
Selectors are set in `hubXmlSelectors.java`  
XML building happens in `hubXmlBuilders.java`  
_USAGE_  
Requires manually setting a few variables in `hubXmlSelectors.java` and soup selectors to make sure we can scrub content, and get all the content and data we need to import a blog into HubSpot. Quite a few other XML elements are set without any selector to set. Running will output a blog.xml file which can be imported into HubSpot using the Blog Importer. If required elements are not able to be found, default values are set, and errors are logged.    

### __variables & soup selectors to set in hubXml/src/main/java/hubXmlSelectors.java__
_Required Elements:_  
There are 3 options for telling hubXml which posts to scrub. Sitemap, listing pagination, and manual listing (order of how easy). Leave the 2 non-used post finding options selectors as empty strings `"""`    
1. SITEMAP - finds posts from sitemaps based on a blog root URL and sitemap. Requires that a site has a sitemap and the posts follow a root url + extension pattern. Ex. `www.blog.com/blog-root-url/post-slug` will find all URLS in the sitemap with `www.blog.com/blog-root-url/<slug>` structure. If using this option, set `BLOG_ROOT_URL`  
2. LISTING PAGINATION - finds posts by jumping through paginated listing pages grabbing post URLs. Requires paginated listing pages with the post URLs. If using this option set `BLOG_LISTING_URL`, `BLOG_LISTING_LINKS_SELECTOR` & `BLOG_LISTING_PAGINATOR`. NOTE: If your listing page is not paginated, and has all of your posts on one page, just set `BLOG_LISTING_PAGINATOR` to be `meep` so hubXml does not find a second page    
3. MANUAL LISTING - requires manually setting the `POSTS` array with the posts for hubXml to scrub  

Below, find the _soup_ selectors which you need to set as CSS selectors for the elements to find. Included are examples of the html element selected --> XML conversion:  
`static final String TITLE_SELECTOR = "title";` - Grabs the title of the post  
```
<title>Becoming the Dragon Warrior</title>
CONVERTS TO >>>>>
<title>Becoming the Dragon Warrior</title> 
```
`static final String DATE_SELECTOR = ".published";` - Grabs the publish date of the post
```
<span class="published">July 2, 2018</span
CONVERTS TO >>>>>
<pubDate>Mon, 02 Jul 2018 00:00:00 +0000</pubDate>
```
`static final String META_DESCRIPTION_SELECTOR = "meta[name=description]";` - Grabs the meta description of the post  
```
<meta name="description" content="Becoming the dragon warrior you were always meant to be"> 
CONVERTS TO >>>>>
<excerpt:encoded><![CDATA[Becoming the dragon warrior you were always meant to be]]<excerpt:encoded>
```
`static final String AUTHOR_SELECTOR = "a[rel=author]";` - Grabs the author of the post  
```
<a href="link" rel="author">Master Shifu</a>
CONVERTS TO >>>>>
<dc:creator>Master Shifu</dc:creator>
&
<wp:author>
    <wp:author_display_name><![CDATA[Master Shifu]]></wp:author_display_name>
    <wp:author_login><![CDATA[Master Shifu]]></wp:author_login>
</wp:author>
```
`static final String TAGS_SELECTOR = "a[rel=category tag]";` - Grabs the tags of the post    
```
<a href="link" rel="category tag">Wuxi Finger Hold</a>
>>>>>
<category domain="category" nicename="Wuxi-Finger-Hold"><![CDATA[Wuxi Finger Hold]]></category>
```
`static final String POST_BODY_SELECTOR = ".post-body";` - Grabs the content of the post   
_NOTE:_ you can set `POST_BODY_SELECTOR_REMOVER` to an element(s) to be removed from the post body, if there is no post body wrapper or some element needs to be removed. Example:  
`static final String[] POST_BODY_SELECTOR_REMOVER = {".social-sharing",".post-body-cta"};`
```
<div class=".post-body">You must find <strong>inner peace</strong> to be an affective dragon warrior... and eat lost of dumplings</div>
CONVERTS TO >>>>>
<content:encoded><![CDATA[<div class=".post-body">You must find <strong>inner peace</strong> to be an affective dragon warrior... and eat lost of dumplings</div>]]</content:encoded>
```
_Optional Elements:_  
`static final String FEATURED_IMAGE_SELECTOR = ".featured-image";` - Grabs the featured image of the post. Will work for `src` attribute of image element, `content` attribute of meta element (like `meta[property="og:image"]`) or inline css `background`/`background-image` declarations  
```
<img class="featured-image" src="https://www.kungfupanda.com/dumplings/featured-image.jpg">
CONVERTS TO >>>>>
<wp:post_meta>
    <wp:meta_key>_thumbnail_id</wp:meta_key>
    <wp:meta_value><![CDATA[2]]></wp:meta_value>
</wp:post_meta>
& >>>>>
<item>
    <title>https://www.kungfupanda.com/dragon-warrior</title>
    <link>https://www.kungfupanda.com/dumplings/featured-image.jpg</link>
    <wp:post_id>2</wp:post_id>
    <wp:post_parent>1</wp:post_parent>
    <wp:post_type>attachment</wp:post_type>
    <wp:attachment_url>https://www.kungfupanda.com/dumplings/featured-image.jpg</wp:attachment_url>
</item>
```
`static final String COMMENT_WRAPPER_SELECTOR = ".comment";` - The wrapping element of individual comments. Comments are optional, but if comments are found, all child comment elements are required  
`static final String COMMENT_TEXT_SELECTOR = ".comment-content p";` - Grabs comment text  
`static final String COMMENT_AUTHOR_SELECTOR = ".name";` - Grabs commenter name   
`static final String COMMENT_AUTHOR_EMAIL_SELECTOR = ".email";` - Grabs commenter email  
```
<div class="comment">
	<span class="name">Master Oogway</span>
	<span class="email">oogway@thejadepalace.com</span>
	<p class="comment-content">Yesterday is history, tomorrow is a mystery, but today is a gift. That is why it is called the present.</div>
</div>
CONVERTS TO >>>>>
<wp:comment>
	<wp:comment_id>1</wp:comment_id>
	<wp:comment_author>Master Oogway</wp:comment_author>
	<wp:comment_author_email>oogway@thejadepalace.com</wp:comment_author_email>
	<wp:comment_date>2018-07-02 17:49:32</wp:comment_date>
	<wp:comment_content><![CDATA[Yesterday is history, tomorrow is a mystery, but today is a gift. That is why it is called the present.]]></wp:comment_content>
	<wp:comment_approved>1</wp:comment_approved>
</wp:comment>
```
__Example final output :tada:__
```
<?xml version="1.0" encoding="UTF-8"?>
<rss xmlns:content="http://purl.org/rss/1.0/modules/content/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:excerpt="http://wordpress.org/export/1.2/excerpt/" xmlns:wp="http://wordpress.org/export/1.2/">
    <channel>
        <link>https://www.kungfupanda.com</link>
        <wp:author>
            <wp:author_display_name><![CDATA[Master Shifu]]></wp:author_display_name>
            <wp:author_login><![CDATA[Master Shifu]]></wp:author_login>
        </wp:author>
        <item>
            <title>https://www.kungfupanda.com/dragon-warrior</title>
            <link>https://www.kungfupanda.com/dumplings/featured-image.jpg</link>
            <wp:post_id>2</wp:post_id>
            <wp:post_parent>1</wp:post_parent>
            <wp:post_type>attachment</wp:post_type>
            <wp:attachment_url>https://www.kungfupanda.com/dumplings/featured-image.jpg</wp:attachment_url>
        </item>
        <item>
            <title>Becoming the Dragon Warrior</title>
            <link>https://www.kungfupanda.com/dragon-warrior</link>
            <pubDate>Mon, 02 Jul 2018 00:00:00 +0000</pubDate>
            <wp:post_id>1</wp:post_id>
            <wp:status>publish</wp:status>
            <wp:post_type>post</wp:post_type>
            <excerpt:encoded><![CDATA[Becoming the dragon warrior you were always meant to be]]></excerpt:encoded>
            <dc:creator>Master Shifu</dc:creator>
            <category domain="category" nicename="Wuxi-Finger-Hold"><![CDATA[Wuxi Finger Hold]]></category>
            <content:encoded><![CDATA[<div>You must find <strong>inner peace</strong> to be an affective dragon warrior... and eat lost of dumplings</div>]]></content:encoded>
            <wp:post_meta>
                <wp:meta_key>_thumbnail_id</wp:meta_key>
                <wp:meta_value><![CDATA[2]]></wp:meta_value>
            </wp:post_meta>
            <wp:comment>
            	<wp:comment_id>1</wp:comment_id>
            	<wp:comment_author>Master Oogway</wp:comment_author>
            	<wp:comment_author_email>oogway@thejadepalace.com</wp:comment_author_email>
            	<wp:comment_date>2018-07-02 17:49:32</wp:comment_date>
            	<wp:comment_content><![CDATA[Yesterday is history, tomorrow is a mystery, but today is a gift. That is why it is called the present.]]></wp:comment_content>
            	<wp:comment_approved>1</wp:comment_approved>
            </wp:comment>
            ~~~ the rest of the <item>'s comments ~~~
        </item>
        ~~~ the rest of your post <item>s ~~~
    </channel>
</rss>

```
