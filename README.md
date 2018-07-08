# hubXml
A tool to turn any blog into a HubSpot importable XML file. It will grab post titles, meta descriptions, authors, tags, featured images and post bodies, and turn them all into importable `<items>`, building a HubSpot importable XML file. Plain and simple, this allows you to __import any blog into HubSpot__, not just Wordpress blogs :tada:

## hubXml/src/main/java/hubXml.java
Selectors are set in `hubXmlSelectors.java`  
XML building happens in `hubXmlBuilders.java`  
_USAGE_  
Requires manually setting a few variables in `hubXmlSelectors.java` and soup selectors to make sure we can scrub content, and get all the content and data we need to import a blog into HubSpot. Quite a few other XML elements are set without any selector to set. Running will output a blog.xml file which can be imported into HubSpot using the Blog Importer.    

### __variables & soup selectors to set in hubXml/src/main/java/hubXmlSelectors.java__
`static final String ROOT_URL` - The root url of the blog you want to turn into an xml file  
`static final String[] POSTS` - An array of blog posts to turn into <item>(s) in the output xml file  
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
```
<div class=".post-body">You must find <strong>inner peace</strong> to be an affective dragon warrior... and eat lost of dumplings</div>
CONVERTS TO >>>>>
<content:encoded><![CDATA[<div class=".post-body">You must find <strong>inner peace</strong> to be an affective dragon warrior... and eat lost of dumplings</div>]]</content:encoded>
```
`static final String FEATURED_IMAGE_SELECTOR = ".featured-image";` - Grabs the featured image of the post (NOTE: the image url must be in the html of the page as a `src` attribute of `<img>` tag. It is also possible to grab inline `background` CSS declarations, but requires some modifications to the `String featuredImageUri` in `hubXml/src/main/java/hubXmlBuilders.java`,  a commented out example is in there)
```
<img class="featured-image" src="https://www.kungfupanda.com/dumplings/featured-image.jpg">
CONVERTS TO >>>>>
<wp:post_eta>
    <wp:meta_key>_thumbnail_id</wp:meta_key>
    <wp:meta_value><![CDATA[2]]></wp:meta_value>
</wp:post_eta>
& >>>>>
<item>
    <title>https://www.kungfupanda.com/dragon-warrior</title>
    <link>https://www.kungfupanda.com/dumplings/featured-image.jpg</link>
    <wp:post_id>2</wp:post_id>
    <wp:post_parent>1</wp:post_parent>
    <wp:post_type>attachment</wp:post_type>
    <wp:attachment_url>https://www.awesomeblog.com/featured-image.jpg</wp:attachment_url>
</item>
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
            <dc:creator>Author</dc:creator>
            <category domain="category" nicename="Wuxi-Finger-Hold"><![CDATA[Wuxi Finger Hold]]></category>
            <content:encoded><![CDATA[<div>You must find <strong>inner peace</strong> to be an affective dragon warrior... and eat lost of dumplings</div>]]></content:encoded>
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