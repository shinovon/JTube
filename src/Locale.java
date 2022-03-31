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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class Locale implements LocaleConstants {
	
	public static final String systemLocale;
	public static boolean loaded;
	private static int localei;
	private static Hashtable table;
	public static String l;
	
	static {
		// J9, JRE language property
		String s = System.getProperty("user.language");
		if(s == null) {
			s = System.getProperty("microedition.locale");
			if(s == null)  {
				s = "en";
			}
		}
		if(s.length() >= 2) {
			s = s.substring(0, 2);
		}
		systemLocale = s.toLowerCase();
		if(!s.equals("en") && (s.equals("ru")
				|| s.equals("uk") || s.equals("be") || s.equals("kk")
				|| s.equals("ua") || s.equals("by") || s.equals("kz"))) {
			localei = 1;
			l = "ru";
		} else {
			localei = 0;
			l = "en";
		}
	}
	
	public static void init() {
		String s = App.customLocale;
		boolean b = true;
		if(s == null || (s = s.trim()).length() == 0) {
			s = systemLocale;
			b = false;
		}
		InputStream in = null;
		try {
			in = Locale.class.getResourceAsStream("/jtlng." + s.toLowerCase());
		} catch (Exception e) {
		}
		if(in != null) {
			DataInputStream d = new DataInputStream(in);
			table = new Hashtable();
			try {
				try {
					int i;
					while( (i = d.readShort()) != -1) {
						Integer n = new Integer(i);
						String sl = d.readUTF();
						if(table.containsKey(n)) continue;
						table.put(n, sl);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					d.close();
				}
				loaded = true;
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} else {
			if(b) {
				if(s.equals("ru")) {
					localei = 1;
				} else if(s.equals("en")) {
					localei = 0;
				}
			} else {
				s = localei == 0 ? "en" : localei == 1 ? "ru" : "unk";
			}
		}
		l = s;
		s = null;
	}
	
	public static String s(int c) {
		Integer i = new Integer(c);
		if(loaded && table.containsKey(i)) {
			return (String) table.get(i);
		}
		// Author
		if(c == 0 && !loaded) {
			return "Shinovon";
		}
		switch(localei) {
		case 0: {
			switch(c) {
			case CMD_Settings:
				return "Settings";
			case CMD_Search:
				return "Search";
			case CMD_OK:
				return "OK";
			case CMD_Cancel:
				return "Cancel";
			case CMD_Back:
				return "Back";
			case CMD_Exit:
				return "Exit";
			case CMD_Apply:
				return "Apply";
			case CMD_Go:
				return "Go";
			case CMD_View:
				return "View";
			case CMD_Watch:
				return "Watch";
			case CMD_Download:
				return "Download";
			case CMD_OpenByID:
				return "Open by ID";
			case CMD_Open:
				return "Open";
			case CMD_Videos:
				return "Videos";
			case CMD_ViewChannel:
				return "View channel";
			case CMD_SwitchToPopular:
				return "Switch to popular";
			case CMD_SwitchToTrends:
				return "Switch to trends";
			case SET_VideoRes:
				return "Preferred video quality";
			case SET_Appearance:
				return "Appearance";
			case SET_OtherSettings:
				return "";
			case SET_DownloadDir:
				return "Download directory";
			case SET_InvAPI:
				return "Invidious API Instance";
			case SET_StreamProxy:
				return "Stream proxy server";
			case SET_ImagesProxy:
				return "Images proxy prefix";
			case SET_CountryCode:
				return "Country code (ISO 3166)";
			case TITLE_Trends:
				return "Trending";
			case TITLE_Popular:
				return "Popular";
			case TITLE_SearchQuery:
				return "Search query";
			case TITLE_Settings:
				return "Settings";
			case BTN_LatestVideos:
				return "Latest videos";
			case BTN_SearchVideos:
				return "Search videos";
			case TITLE_Loading:
				return "Loading";
			case TXT_Views:
				return "Views";
			case TXT_LikesDislikes:
				return "Likes / Dislikes";
			case TXT_Published:
				return "Published";
			case TXT_Description:
				return "Description";
			case BTN_ChannelInformation:
				return "Information";
			case TXT_Connecting:
				return "Connecting";
			case TXT_Waiting:
				return "Error! Waiting for retry...";
			case TXT_ConnectionRetry:
				return "Connection retry";
			case TXT_Redirected:
				return "Redirected";
			case TXT_Connected:
				return "Connected";
			case TXT_Downloading:
				return "Downloading";
			case TXT_Downloaded:
				return "Downloaded";
			case TXT_Canceled:
				return "Canceled";
			case TXT_DownloadFailed:
				return "Download failed";
			case TXT_Initializing:
				return "Initializing";
			case TXT_Done:
				return "Done";
			case CMD_About:
				return "About";
			case CMD_Select:
				return "Select";
			case CMD_OpenPlaylist:
				return "Open playlist";
			case CMD_Next:
				return "Next video";
			case CMD_Prev:
				return "Prev. video";
			case SET_CustomLocaleId:
				return "Custom locale identificator";
			case SET_CustomItems:
				return "Better items";
			case SET_HTTPProxy:
				return "HTTP Proxy Streaming";
			case SET_PreLoadRMS:
				return "Pre-load images to RMS";
			case SET_RememberSearch:
				return "Remember search";
			case SET_VideoPreviews:
				return "Video previews";
			case SET_SearchChannels:
				return "Search channels";
			case SET_SearchPlaylists:
				return "Search playlists";
			case SET_VQ_AudioOnly:
				return "Audio only";
			case SET_VQ_NoAudio:
				return "no audio";
			case SET_Tip1:
				return "(Used only if http streaming is on)";
			case SET_Tip2:
				return "(Leave images proxy empty if HTTPS is supported)";
			case BTN_Playlists:
				return "Playlists";
			case CMD_ShowLink:
				return "Show link";
			case SET_Tip3:
				return "(Always used for online playback, and for downloading if HTTP streaming is enabled)";
			case SET_PlaybackMethod:
				return "Playback method";
			case SET_SymbianOnline:
				return "Online player (Symbian/Bada)";
			case SET_Browser:
				return "Via browser";
			case SET_DownloadBuffer:
				return "Download buffer size (bytes)";
			case TXT_VideoDuration:
				return "Video duration";
			}
		}
		case 1: {
			switch(c) {
			case CMD_Settings:
				return "Настройки";
			case CMD_Search:
				return "Поиск";
			case CMD_OK:
				return "OK";
			case CMD_Cancel:
				return "Отмена";
			case CMD_Back:
				return "Назад";
			case CMD_Exit:
				return "Выйти";
			case CMD_Apply:
				return "Применить";
			case CMD_Go:
				return "Открыть";
			case CMD_View:
				return "Открыть";
			case CMD_Watch:
				return "Смотреть";
			case CMD_Download:
				return "Скачать";
			case CMD_OpenByID:
				return "Открыть по ссылке";
			case CMD_Open:
				return "Открыть";
			case CMD_Videos:
				return "Видео";
			case CMD_ViewChannel:
				return "Открыть канал";
			case CMD_SwitchToPopular:
				return "Сменить на популярные";
			case CMD_SwitchToTrends:
				return "Сменить на тренды";
			case SET_VideoRes:
				return "Предпочитаемое качество видео";
			case SET_Appearance:
				return "Внешность";
			case SET_OtherSettings:
				return "";
			case SET_DownloadDir:
				return "Папка для скачивания";
			case SET_InvAPI:
				return "Invidious API Instance";
			case SET_StreamProxy:
				return "Stream proxy server";
			case SET_ImagesProxy:
				return "Прокси для картинок";
			case SET_CountryCode:
				return "Код страны (ISO 3166)";
			case TITLE_Trends:
				return "Тренды";
			case TITLE_Popular:
				return "Популярные";
			case TITLE_SearchQuery:
				return "Результаты поиска";
			case TITLE_Settings:
				return "Настройки";
			case BTN_LatestVideos:
				return "Последние видео";
			case BTN_SearchVideos:
				return "Поиск видео";
			case TITLE_Loading:
				return "Загрузка";
			case TXT_Views:
				return "Просмотры";
			case TXT_LikesDislikes:
				return "Понравилось / Не понравилось";
			case TXT_Published:
				return "Выпущено";
			case TXT_Description:
				return "Описание";
			case BTN_ChannelInformation:
				return "Информация";
			case TXT_Connecting:
				return "Соединение";
			case TXT_Waiting:
				return "Ошибка подключения! Ожидание...";
			case TXT_ConnectionRetry:
				return "Повторная попытка подключения";
			case TXT_Redirected:
				return "Перенаправлен";
			case TXT_Connected:
				return "Подключен";
			case TXT_Downloading:
				return "Скачивание";
			case TXT_Downloaded:
				return "Скачано";
			case TXT_Canceled:
				return "Отменено";
			case TXT_DownloadFailed:
				return "Скачивание не удалось";
			case TXT_Initializing:
				return "Инициализация";
			case TXT_Done:
				return "Готово";
			case CMD_About:
				return "О программе";
			case CMD_Select:
				return "Выбрать";
			case CMD_OpenPlaylist:
				return "Откр. плейлист";
			case CMD_Next:
				return "След. видео";
			case CMD_Prev:
				return "Пред. видео";
			case SET_CustomLocaleId:
				return "Идентификатор польз. локализации";
			case SET_CustomItems:
				return "Улучшенный вид";
			case SET_HTTPProxy:
				return "HTTP прокси стриминг";
			case SET_PreLoadRMS:
				return "Предзагрузка изображений в RMS";
			case SET_RememberSearch:
				return "Запоминание поиска";
			case SET_VideoPreviews:
				return "Изображения";
			case SET_SearchChannels:
				return "Поиск каналов";
			case SET_SearchPlaylists:
				return "Поиск плейлистов";
			case SET_VQ_AudioOnly:
				return "Только аудио";
			case SET_VQ_NoAudio:
				return "без звука";
			case SET_Tip1:
				return "(Использован только если включен HTTP стриминг через прокси)";
			case SET_Tip2:
				return "(Оставьте пустым если ваше устройство поддерживает HTTPS)";
			case BTN_Playlists:
				return "Плейлисты";
			case CMD_ShowLink:
				return "Показать ссылку";
			case SET_Tip3:
				return "(Используется всегда при онлайн проигрывании, и для скачивания если включен HTTP стриминг)";
			case SET_PlaybackMethod:
				return "Способ проигрывания";
			case SET_SymbianOnline:
				return "Онлайн плеер (Symbian/Bada)";
			case SET_Browser:
				return "Через браузер";
			case SET_DownloadBuffer:
				return "Размер буфера скачивания (байты)";
			case TXT_VideoDuration:
				return "Длительность видео";
			}
		}
		}
		return null;
	}
	
	public static String subscribers(int i) {
		if(i <= 0) return null;
		if(loaded) {
			if(i == 1) return i + " " + s(TXT_1subscriber);
			if(i % 10 == 1) return i + " " + s(TXT_10_1subscribers);
			return i + " " + s(TXT_subscribers);
		}
		if(localei == 1) {
			if(i % 10 == 1) return i + " подписчик";
			return i + " подписчиков";
		}
		if(i == 1) return i + " subscriber";
		return i + " subscribers";
	}
	
	public static String views(int i) {
		if(i >= 1000000) {
			return ((int) ((i / 1000000D) * 100) / 100D) + " M";
		}
		return "" + i;
	}

	public static String videos(int i) {
		if(i <= 0) return null;
		if(loaded) {
			if(i == 1) return i + " " + s(TXT_1video);
			return i + " " + s(TXT_videos);
		}
		if(localei == 1) {
			return i + " видео";
		}
		if(i == 1) return i + " video";
		return i + " videos";
	}

}
