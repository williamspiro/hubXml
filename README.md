# hubXml
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
<title>This is the post title</title>
>>>>>
<title>This is the post title</title> 
```
`public static final String META_DESCRIPTION_SELECTOR = "meta[name=description]";` - Grabs the meta description of the post  
```
<meta name="description" content="This is the meta description"> 
>>>>>
<excerpt:encoded><![CDATA[This is the meta description]]<excerpt:encoded>
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
`public static final String POST_BODY_SELECTOR = ".entry-content";` - Grabs the content of the post  
```
<div class="entry-content">This is the post body</div>
>>>>>
<content:encoded><![CDATA[This is the post body]]</content:encoded>
```

XML setup which happens on its own:
```
<?xml version='1.0' encoding='UTF-8'?>
<rss>
  <channel>
  <link>!!blog root url link from blogRootUrl variable!!</link>
  <
    !!<wp:author>s build here!!
  >
    <item>
        <link>!!post link from blogPosts library!!</link>
        <wp:post_id>!!set automatically!!</wp:post_id>
        <wp:status>publish</wp:status>
        <wp:post_type>post</wp:post_type>
        <
            !!The above soups fill in XML here!!
        >
    </item>
    ... !!for every post in blogPosts library, the above <item> is created!!
  </channel>
</rss>
```

There is some dummy `ROOT_URL` and `POSTS` in the hubXml.java file, if you want to see it in action, try:
```
public static final String ROOT_URL = "https://coolwebsitedotcom.wordpress.com";
public static final String[] POSTS = {"https://coolwebsitedotcom.wordpress.com/2018/07/02/blog-post-1/", "https://coolwebsitedotcom.wordpress.com/2018/07/02/blog-post-2/", "https://coolwebsitedotcom.wordpress.com/2018/07/02/blog-post-3/"};
```
