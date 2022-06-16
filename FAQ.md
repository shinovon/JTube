# FAQ
## Questions & Unswers
<b>Is it possible to select Opera as the browser for video playback?</b><br>
No. Java is very limited in the ability to open third-party applications, and this makes no sense.<br>

<b>No sound (S40)</b><br>
The device does not support the audio codec issued by YouTube

<b>Where can I get the language editor for JTube?</b><br>
You can find it in telegram chat: https://t.me/nnmidletschat<br>
Latest (2.0.2-2.0.5): https://t.me/nnmidletschat/1043

## Errors
### Common
<b>java.lang.SecurityException</b><br>
Occurs because the user clicked "no" when prompted to allow network requests or something else<br>

<b>API error</b><br>
Usually this error tells what happened and most often it occurs due to sending too much requests

<b>The page cannot be opened (403)</b><br>
JTube does not currently support auto-generated music videos and vevo

### Symbian
<b>java.io.IOException: -1 (Symbian 9.2-9.4 & Symbian^3)</b><br>
Reason: App can't access the internet<br>
Solutions:
1. Check your internet connection
2. Set network access point for the JTube in App Mgr. (9.2-9.4)
3. Restart your phone

<b>java.io.IOException: Native Error-5</b><br>
Most often occurs on Symbian 9.3 when downloading,<br>
but in most cases this is an unrecoverable error<br>
Possible solution:
1. Set network access point for the JTube in App Mgr.

<b>java.io.IOException: -33</b><br>
Reason: Connection timed out<br>
May be due to poor internet connection<br>

<b>java.io.IOException: -36</b><br>
Reason: Сonnection interrupted/disconnected unexpectedly<br>
May be due to poor internet connection<br>

<b>java.io.IOException: -5120</b><br>
Reason: Site's DNS was not found<br>
No internet connection<br>

### Series 40
<b>java.io.IOException: 81-Error in HTTP operation (Series 40)</b><br>
Reason: App can't access the internet<br>
Solution:
1. Set up APN settings for apps in device settings
