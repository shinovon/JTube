# JTube
Youtube client based on Invidious API for Java devices with MIDP 2.0 support
## Setting up your own server
First you need to install NGINX server if you don't already have it installed

Also curl-ext is important in php

stream.php, Http streamer (https://github.com/Athlon1600/youtube-downloader must be installed):
```<?php
include(__DIR__. '/../vendor/autoload.php');
set_time_limit(0);
$url = isset($_GET['url']) ? $_GET['url'] : null;
if ($url == false) {
    die("No url provided");
}
$youtube = new \YouTube\YouTubeStreamer();
$youtube->stream($url);
?>
```

hproxy.php (Image proxy):
```
<?php

function reqHeaders($arr, $url = null) {
	$res = array();
	foreach($arr as $k=>$v) {
		$lk = strtolower($k);
		if($lk == 'host' && isset($url)) {
			$dom = '';
			if(startsWith($url, 'http://')) {
				$dom = substr($url, 7);
			} else if(startsWith($url, 'https://')) {
				$dom = substr($url, 8);
			} else {
				$dom = $url;
			}
			$pos = strpos($dom, '/');
			if($pos) {
				$dom = substr($dom, 0, $pos);
			}
			array_push($res, 'Host: '. $dom);
		} else if($lk != 'connection' && $lk != 'accept-encoding' && $lk != 'user-agent') {
			array_push($res, $k . ': ' . $v);
		}
	}
	return $res;
}

function handleHeaders($str) {
	$headersTmpArray = explode("\r\n", $str);
	for ($i = 0; $i < count($headersTmpArray); ++$i) {
		$s = $headersTmpArray[$i];
		if(strlen($s) > 0) {
			if(strpos($s, ":")) {
				$k = substr($s, 0 , strpos($s, ":"));
				$v = substr($s, strpos($s, ":" )+1);
				$lk = strtolower($k);
				if(/*$lk != 'server' && */$lk != 'connection' && $lk != 'transfer-encoding' && $lk != 'location') {
					header($s, true);
				}
			}
		}
	}
}
$url = urldecode($_SERVER['QUERY_STRING']);
$lu = strtolower($url);

if(substr($lu, 0, 5) == 'file:' || substr($lu, 0, 4) == "ftp:") {
	return;
}
unset($lu);
$reqheaders = reqHeaders(getallheaders(), $url);
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
if(isset($_SERVER['HTTP_USER_AGENT']))
	curl_setopt($ch, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT']);
curl_setopt($ch, CURLOPT_HTTPHEADER, $reqheaders);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_HEADER, true);
$res = curl_exec($ch);
$headerSize = curl_getinfo($ch, CURLINFO_HEADER_SIZE);
$header = substr($res, 0, $headerSize);
$body = substr($res, $headerSize);
handleHeaders($header);
curl_close($ch);
echo $body;
?>
```
