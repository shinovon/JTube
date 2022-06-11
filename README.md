# JTube
Youtube client based on Invidious API for Java devices with MIDP 2.0 support

<a href="faq.md">FAQ</a><br>

JTube Lite:
https://github.com/shinovon/JTubeLite
## Building

```
You will need:
S40 5th Edition SDK
JDK 1.5.0 (to run S40 emulator)
Last version of Eclipse SDK
MTJ plugin 2.0.1
```

Clone the repository<br>

Import project from directory in Eclipse SDK<br>

Open "Application Descriptor" in Package Explorer
![image](https://user-images.githubusercontent.com/43963888/154848600-b6f30e9c-a412-4771-80bf-527afe11076e.png)<br>

Click on "Create package"
![image](https://user-images.githubusercontent.com/43963888/154848614-72752480-b988-40cd-a3c6-9cad1e02d77c.png)<br>

Check the "Use deployment directory"<br>

Uncheck "Obfuscate the code" if you don't want to optimize code<br>

Then press "Finish"<br>

![image](https://user-images.githubusercontent.com/43963888/154848648-2f054800-b72e-49e6-8b6c-7e3cb6d3c216.png)<br>

Builded JAR & JAD files will appear at \<project path\>/deployed/S40_5th_Edition_SDK/

## Setting up your own server
Glype proxy:
https://github.com/vincentclee/glype

stream.php (HTTP Streaming proxy for older versions):
https://github.com/Athlon1600/youtube-downloader

hproxy.php (Image proxy):
```
<?php

function reqHeaders($arr, $url = null) {
	$res = array();
	foreach($arr as $k=>$v) {
		$lk = strtolower($k);
		if($lk == 'host' && isset($url)) {
			$dom = '';
			if(strpos($url, 'http://') == 0) {
				$dom = substr($url, 7);
			} else if(strpos($url, 'https://') == 0) {
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
