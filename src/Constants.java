/*
Copyright (c) 2022 Arman Jussupgaliyev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public interface Constants {
	
	// default urls
	static final String hproxy = "http://nnp.nnchan.ru/proxy.php?";
	static final String iteroni = "http://iteroni.com/";
	static final String stream = "http://nnp.nnchan.ru/stream.php?url=";
	static final String glype = "http://nnp.nnchan.ru/glype/browse.php?u=";
	static final String updateurl = "http://nnp.nnchan.ru/jtubeupd.php";
	
	static final String CONFIG_RECORD_NAME = "ytconfig";
	
	// Limits
	static final int TRENDS_LIMIT = 20;
	static final int SEARCH_LIMIT = 25;
	static final int LATESTVIDEOS_LIMIT = 20;
	static final int PLAYLISTS_LIMIT = 30;
	static final int PLAYLIST_VIDEOS_LIMIT = 100;
	
	static final String NAME = "JTube";
	
	static final String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0";
	
	static final String VIDEO_EXTENDED_FIELDS = "title,videoId,author,authorId,description,videoCount,publishedText,lengthSeconds,likeCount,dislikeCount,viewCount,subCount";
	static final String CHANNEL_EXTENDED_FIELDS = "subCount,author,authorId,description,totalViews";
	static final String PLAYLIST_EXTENDED_FIELDS = "author,authorId,videoCount,videos,title,videoId,playlistId,index,lengthSeconds";
	static final String VIDEO_FIELDS = "title,videoId,author,lengthSeconds";
	static final String SEARCH_FIELDS = "title,authorId,videoId,author,lengthSeconds";
	
	/** @deprecated */
	static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");
	static final String PATH_SEPARATOR = "/";
	static final String EOL = SYSTEM_LINE_SEPARATOR != null ? SYSTEM_LINE_SEPARATOR : "\n";
	
	static final String CONTANT_DEVELOPER_STRING = "Contact developer: t.me/nnmidletschat";
	
	static final int DEFAULT_PUSH_PORT = 1260;

}
