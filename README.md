# hubXml meep
A tool to turn any externally hosted blog into a HubSpot importable XML file. This is a Java port of [externalBlawgDawg.py](https://github.com/williamspiro/blawgDawg/blob/master/externalBlawgDawg.py).

## hubXml/src/main/java/hubXml.java
_USAGE_  
Requires manually setting a few variables in `hubXmlVariables.java` and soup selectors to make sure we can scrub external content, and get all the content and data we need to import a blog into HubSpot. It will grab post titles, urls, meta descriptions, authors, tags and post bodies, and turn them all into importable `<items>`. Running will output a blog.xml file which can be imported into HubSpot using the Blog Importer.    

### __variables & soup selectors to set in hubXml/src/main/java/hubXmlVariables.java__
`public static final String ROOT_URL` - The root url of the external blog you want to turn into an xml file  
`public static final String[] POSTS` - An array of external blog posts to turn into <item>(s) in the output xml file  
Below, find the _soup_ selectors which you need to set as CSS selectors for the elements to find. Included are examples of the html element selected --> XML conversion:
```
[html from scrubbed post]
>>>>>
[xml output form hubXml.java]
```
`public static final String TITLE_SELECTOR = "title";` - Grabs the title of the post  
```
<title>Awesome Blog Post</title>
>>>>>
<title>Awesome Blog Post</title> 
```
`public static final String META_DESCRIPTION_SELECTOR = "meta[name=description]";` - Grabs the meta description of the post  
```
<meta name="description" content="This is the meta description of my awesome post!"> 
>>>>>
<excerpt:encoded><![CDATA[This is the meta description of my awesome post!]]<excerpt:encoded>
```
`public static final String AUTHOR_SELECTOR = "a[rel=author]";` - Grabs the author of the post  
```
<a href="link" rel="author">Author</a>
>>>>>
<dc:creator>Author</dc:creator>
&
<wp:author>
    <wp:author_display_name><![CDATA[Author]]></wp:author_display_name>
    <wp:author_login><![CDATA[Author]]></wp:author_login>
</wp:author>
```
`public static final String TAGS_SELECTOR = "a[rel=category tag]";` - Grabs the tags of the post  
```
<a href="link" rel="category tag">Tag 1</a>
<a href="link" rel="category tag">Tag 2</a>
>>>>>
<category domain="category" nicename="tag-1"><![CDATA[Tag 1]]></category>
<category domain="category" nicename="tag-2"><![CDATA[Tag 2]]></category>
```
`public static final String POST_BODY_SELECTOR = ".post-body";` - Grabs the content of the post  
```
<div class=".post-body">This is the post body of my awesome post!</div>
>>>>>
<content:encoded><![CDATA[<div class=".post-body">This is the post body of my awesome post!</div>]]</content:encoded>
```
`public static final String FEATURED_IMAGE_SELECTOR = ".featured-image";` - Grabs the featured image of the post (NOTE: the image url must be in the html of the page as a `src` attribute of `<img>` tag. It is also possible to grab inline `background` CSS declarations, but requires some modifications to the `String featuredImageUri` in `hubXml/src/main/java/hubXml.java`)
```
<img class="featured-image" src="https://www.awesomeblog.com/featured-image.jpg">
>>>>>
<wp:post_eta>
    <wp:meta_key>_thumbnail_id</wp:meta_key>
    <wp:meta_value><![CDATA[2]]></wp:meta_value>
</wp:post_eta>
&
<item>
    <title>https://www.awesomeblog.com/awesome-post</title>
    <link>https://www.awesomeblog.com/featured-image.jpg</link>
    <wp:post_id>2</wp:post_id>
    <wp:post_parent>1</wp:post_parent>
    <wp:post_type>attachment</wp:post_type>
    <wp:attachment_url>https://www.awesomeblog.com/featured-image.jpg</wp:attachment_url>
</item>

```
XML setup which happens on its own:
```
<?xml version='1.0' encoding='UTF-8'?>
<rss>
    <channel>
        <link>!!~~blog root url link from ROOT_URL variable~~!!</link>
        <
            !!<wp:author>s build here!!
        >
        <
            !!~~featured images <item>s build here~~!!
        >
        <item>
            <link>!!~~post link from POSTS array~~!!</link>
            <wp:post_id>!!~~set automatically~~!!</wp:post_id>
            <wp:status>publish</wp:status>
            <wp:post_type>post</wp:post_type>
            <
                !!~~The above soups fill in XML here~~!!
            >
        </item>
        ... !!~~for every post in POSTS array, the above <item> is created~~!!
    </channel>
</rss>
```
__Example final output :tada::rocket::dancers:__
```
<?xml version="1.0" encoding="UTF-8"?>
<rss xmlns:content="http://purl.org/rss/1.0/modules/content/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:excerpt="http://wordpress.org/export/1.2/excerpt/" xmlns:wp="http://wordpress.org/export/1.2/">
    <channel>
        <link>https://www.awesomeblog.com</link>
        <wp:author>
            <wp:author_display_name><![CDATA[Author]]></wp:author_display_name>
            <wp:author_login><![CDATA[Author]]></wp:author_login>
        </wp:author>
        <item>
            <title>https://www.awesomeblog.com/awesome-post</title>
            <link>https://www.awesomeblog.com/featured-image.jpg</link>
            <wp:post_id>2</wp:post_id>
            <wp:post_parent>1</wp:post_parent>
            <wp:post_type>attachment</wp:post_type>
            <wp:attachment_url>https://www.awesomeblog.com/featured-image.jpg</wp:attachment_url>
        </item>
        <item>
            <title>Awesome Blog Post</title>
            <link>https://www.awesomeblog.com/awesome-post</link>
            <pubDate>Wed, 25 Apr 2018 13:19:35 +0000</pubDate>
            <wp:post_id>1</wp:post_id>
            <wp:status>publish</wp:status>
            <wp:post_type>post</wp:post_type>
            <excerpt:encoded><![CDATA[This is the meta description of my awesome post!]]></excerpt:encoded>
            <dc:creator>Author</dc:creator>
            <category domain="category" nicename="This-is-a-tag">This is a tag</category>
            <content:encoded><![CDATA[<div>This is the post body of my awesome post!</div>]]></content:encoded>
            <wp:post_meta>
                <wp:meta_key>_thumbnail_id</wp:meta_key>
                <wp:meta_value><![CDATA[2]]></wp:meta_value>
            </wp:post_meta>
        </item>
        <item>
            ... the rest of your post <items>...
        </item>
    </channel>
</rss>

```
