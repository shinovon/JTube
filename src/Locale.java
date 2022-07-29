
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
	protected static int localei;
	private static Hashtable table;
	public static String l;

	static {
		// J9, JRE language property
		String s = System.getProperty("user.language");
		if (s == null) {
			s = System.getProperty("microedition.locale");
			if (s == null) {
				s = "en";
			}
		}
		if (s.length() >= 2) {
			s = s.substring(0, 2);
		}
		systemLocale = s.toLowerCase();
		if (!s.equals("en") && (s.equals("ru") || s.equals("uk") || s.equals("be") || s.equals("kk") || s.equals("ua")
				|| s.equals("by") || s.equals("kz"))) {
			localei = 1;
			l = "ru";
		} else {
			localei = 0;
			l = "en";
		}
	}

	public static void init() {
		String s = Settings.customLocale;
		boolean b = true;
		if (s == null || (s = s.trim()).length() == 0) {
			s = systemLocale;
			b = false;
		}
		InputStream in = null;
		try {
			in = Locale.class.getResourceAsStream("/jtlng_" + s.toLowerCase());
		} catch (Exception e) {
		}
		if (in != null) {
			DataInputStream d = new DataInputStream(in);
			table = new Hashtable();
			try {
				try {
					int i;
					while ((i = d.readShort()) != -1) {
						Integer n = new Integer(i);
						String sl = d.readUTF();
						if (table.containsKey(n))
							continue;
						table.put(n, sl);
					}
				} catch (IOException e) {
				} finally {
					d.close();
				}
				loaded = true;
			} catch (IOException e) {
			}
		} else {
			if (b) {
				if (s.equals("ru")) {
					localei = 1;
				} else if (s.equals("en")) {
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
		if (loaded && table.containsKey(i)) {
			return (String) table.get(i);
		}
		// Author
		if (c == 0 && !loaded) {
			return "Shinovon";
		}
		switch (localei) {
		case 0: {
			switch (c) {
			case ISOLanguageCode:
				return "en-US";
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
				return "Misc";
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
				return "Online (Symbian/Bada)";
			case SET_Browser:
				return "Via browser";
			case SET_DownloadBuffer:
				return "Download buffer size (bytes)";
			case TXT_VideoDuration:
				return "Video duration";
			case SET_Via2yxa:
				return "Via 2yxa.mobi";
			case SET_CheckUpdates:
				return "Check for updates";
			case TXT_NewUpdateAvailable:
				return "New update available!";
			case CMD_Ignore:
				return "Ignore";
			case SET_On:
				return "On";
			case SET_Off:
				return "Off";
			case SET_IteroniProxy:
				return "Use iteroni proxy for playback";
			case CMD_Func:
				return "Menu";
			case CMD_Refresh:
				return "Refresh";
			case SET_Amoled:
				return "Night theme";
			case SET_SmallPreviews:
				return "Small previews";
			case SET_Reset:
				return "Reset settings";
			case SET_Video:
				return "Video";
			case SET_Network:
				return "Network";
			case TXT_SearchHint:
				return "Search..";
			case SET_AutoStart:
				return "Auto-start from other applications";
			case BTN_Share:
				return "Share";
			case SET_ChooseLanguage:
				return "Choose language";
			case SET_FullScreenMode:
				return "Full-Screen mode";
			}
		}
		case 1: {
			switch (c) {
			case ISOLanguageCode:
				return "ru-RU";
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
				return "Прочие настройки";
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
				return "Онлайн (Symbian/Bada)";
			case SET_Browser:
				return "Через браузер";
			case SET_DownloadBuffer:
				return "Размер буфера скачивания (байты)";
			case TXT_VideoDuration:
				return "Длительность видео";
			case SET_Via2yxa:
				return "Через 2yxa.mobi";
			case SET_CheckUpdates:
				return "Проверять наличие обновлений";
			case TXT_NewUpdateAvailable:
				return "Доступно новое обновление!";
			case CMD_Ignore:
				return "Ок";
			case SET_On:
				return "Вкл.";
			case SET_Off:
				return "Выкл.";
			case SET_IteroniProxy:
				return "Прокси iteroni для проигрывания";
			case CMD_Func:
				return "Меню";
			case CMD_Refresh:
				return "Обновить";
			case SET_Amoled:
				return "Ночная тема";
			case SET_SmallPreviews:
				return "Маленькие превью";
			case SET_Reset:
				return "Сбросить настройки";
			case SET_Video:
				return "Видео";
			case SET_Network:
				return "Сеть";
			case TXT_SearchHint:
				return "Поиск..";
			case SET_AutoStart:
				return "Авто-старт из других приложений";
			case BTN_Share:
				return "Поделиться";
			case SET_ChooseLanguage:
				return "Выбрать язык";
			case SET_FullScreenMode:
				return "Полноэкранный режим";
			}
		}
		}
		return null;
	}

	public static String subscribers(int i) {
		if (i <= 0)
			return null;
		String s = "" + i;
		if (loaded) {
			if (i >= 1000000) {
				s = ((int) ((i / 1000000D) * 10) / 10D) + "M";
			} else if (i >= 1000) {
				s = ((int) ((i / 1000000D) * 10) / 10D) + "K";
			}
			s = Util.replace(s, ".0", "");
			if (i == 1)
				return s + " " + s(TXT_1subscriber);
			if (i % 10 == 1)
				return s + " " + s(TXT_10_1subscribers);
			return s + " " + s(TXT_subscribers);
		}
		if (localei == 1) {
			if (i >= 1000000000) {
				s = ((int) ((i / 1000000000D) * 100) / 100D) + " млрд.";
			} else if (i >= 1000000) {
				s = ((int) ((i / 1000000D) * 100) / 100D) + " млн.";
			} else if (i >= 1000) {
				s = ((int) ((i / 1000D) * 100) / 100D) + " тыс.";
			} else if (i % 10 == 1) {
				return i + " подписчик";
			} else if (i >= 5) {
				return i + " подписчиков";
			} else {
				return i + " подписчика";
			}
			s = Util.replace(s, ".0", "") + " подписчиков";
			return s;
		}
		if (i >= 1000000) {
			s = ((int) ((i / 1000000D) * 10) / 10D) + "M";
		} else if (i >= 1000) {
			s = ((int) ((i / 1000D) * 10) / 10D) + "K";
		}
		s = Util.replace(s, ".0", "");
		if (i == 1)
			return s + " subscriber";
		return s + " subscribers";
	}

	public static String views(int i) {
		if (loaded) {
			String s = "" + i;
			if (i == 1) {
				s += " " + Locale.s(TXT_1view);
			} else if (i >= 1000000) {
				s = ((int) ((i / 1000000D) * 10) / 10D) + "M " + Locale.s(TXT_views);
			} else if (i >= 1000) {
				s = ((int) ((i / 1000D) * 10) / 10D) + "K " + Locale.s(TXT_views);
			} else {
				s += " " + Locale.s(TXT_views);
			}
			s = Util.replace(s, ".0", "");
			return s;
		}
		if (localei == 1) {
			String s = "" + i;
			if (i == 1) {
				s += " просмотр";
			} else if (i >= 1000000000) {
				s = ((int) ((i / 1000000000D) * 10) / 10D) + " млрд. просмотров";
			} else if (i >= 1000000) {
				s = ((int) ((i / 1000000D) * 10) / 10D) + " млн. просмотров";
			} else if (i >= 1000) {
				s = ((int) ((i / 1000D) * 10) / 10D) + " тыс. просмотров";
			} else if ((i % 100 >= 5 && i % 100 <= 20) || i % 10 == 0) {
				s += " просмотров";
			} else {
				s += " просмотра";
			}
			s = Util.replace(s, ".0", "");
			return s;
		}
		String s = "" + i;
		if (i == 1) {
			s += " view";
		} else if (i >= 1000000) {
			s = ((int) ((i / 1000000D) * 10) / 10D) + "M views";
		} else if (i >= 1000) {
			s = ((int) ((i / 1000D) * 10) / 10D) + "K views";
		} else {
			s += " views";
		}
		s = Util.replace(s, ".0", "");
		return s;
	}

	public static String videos(int i) {
		if (i <= 0)
			return null;
		if (loaded) {
			if (i == 1)
				return i + " " + s(TXT_1video);
			return i + " " + s(TXT_videos);
		}
		if (localei == 1) {
			return i + " видео";
		}
		if (i == 1)
			return i + " video";
		return i + " videos";
	}

	public static String date(String s) {
		if (s == null)
			return null;
		if (localei == 1) {
			if (s.indexOf("ago") != -1) {
				try {
					if (s.indexOf("years ago") != -1) {
						int i = Integer.parseInt(s.substring(0, s.indexOf(' ')));
						if (i % 10 == 1) {
							s = "год назад";
						} else if ((i % 100 >= 5 && i % 100 <= 20) || i % 10 == 0) {
							s = "лет назад";
						} else {
							s = "года назад";
						}
						s = i + " " + s;
					} else if (s.indexOf("months ago") != -1) {
						int i = Integer.parseInt(s.substring(0, s.indexOf(' ')));
						if (i % 10 == 1) {
							s = "месяц назад";
						} else if ((i % 100 >= 5 && i % 100 <= 20) || i % 10 == 0) {
							s = "месяцев назад";
						} else {
							s = "месяца назад";
						}
						s = i + " " + s;
					} else if (s.indexOf("weeks ago") != -1) {
						int i = Integer.parseInt(s.substring(0, s.indexOf(' ')));
						if (i % 10 == 1) {
							s = "неделю назад";
						} else if ((i % 100 >= 5 && i % 100 <= 20) || i % 10 == 0) {
							s = "недель назад";
						} else {
							s = "недели назад";
						}
						s = i + " " + s;
					} else if (s.indexOf("days ago") != -1) {
						int i = Integer.parseInt(s.substring(0, s.indexOf(' ')));
						if (i % 10 == 1) {
							s = "день назад";
						} else if ((i % 100 >= 5 && i % 100 <= 20) || i % 10 == 0) {
							s = "дней назад";
						} else {
							s = "дня назад";
						}
						s = i + " " + s;
					} else if (s.indexOf("hours ago") != -1) {
						int i = Integer.parseInt(s.substring(0, s.indexOf(' ')));
						if (i % 10 == 1) {
							s = "час назад";
						} else if ((i % 100 >= 5 && i % 100 <= 20) || i % 10 == 0) {
							s = "часов назад";
						} else {
							s = "часа назад";
						}
						s = i + " " + s;
					} else if (s.indexOf("minutes ago") != -1) {
						int i = Integer.parseInt(s.substring(0, s.indexOf(' ')));
						if (i % 10 == 1) {
							s = "минуту назад";
						} else if ((i % 100 >= 5 && i % 100 <= 20) || i % 10 == 0) {
							s = "минут назад";
						} else {
							s = "минуты назад";
						}
						s = i + " " + s;
					}
					{
						s = Util.replace(s, "year ago", "год назад");
						s = Util.replace(s, "month ago", "месяц назад");
						s = Util.replace(s, "week ago", "неделю назад");
						s = Util.replace(s, "day ago", "день назад");
						s = Util.replace(s, "hour ago", "час назад");
						s = Util.replace(s, "minute ago", "минуту назад");
					}
				} catch (Exception e) {
				}
			}
		}
		return s;
	}

}
