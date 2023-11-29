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
package jtube.ui;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import jtube.Settings;
import jtube.Util;

public class Locale implements LocaleConstants {

	public static boolean loaded;
	public static int localei;
	private static String[] values = new String[512];
	public static String lang;

	public static void init() {
		String sys = System.getProperty("user.language");
		if (sys == null) {
			if ((sys = System.getProperty("microedition.locale")) == null) {
				sys = "en";
			}
		}
		if ((sys = sys.toLowerCase()).length() >= 2) {
			sys = sys.substring(0, 2);
		}
		if (!sys.equals("en") &&
				(sys.equals("ru") || sys.equals("be") ||
				sys.equals("kk") || sys.equals("ua"))
				) {
			localei = 1;
			lang = "ru";
		} else {
			localei = 0;
			lang = "en";
		}
		
		String s = Settings.customLocale;
		boolean b = true;
		if (s == null || (s = s.trim().toLowerCase()).length() == 0) {
			s = sys;
			b = false;
		}
		InputStream in = null;
		try {
			in = Locale.class.getResourceAsStream("/jtlng_".concat(s));
		} catch (Exception e) {
		}
		if (in != null) {
			in = new DataInputStream(in);
			try {
				int i;
				while ((i = ((DataInputStream) in).readShort()) != -1) {
					values[i == ISOLanguageCode ? values.length - 1 : i] = ((DataInputStream) in).readUTF();
				}
				loaded = true;
				lang = s;
				return;
			} catch(IOException e) {
			} finally {
				try { in.close(); } catch (IOException e) {}
			} 
		}
		if (b) {
			if (s.equals("ru")) {
				localei = 1;
				lang = s;
				return;
			}
			localei = 0;
			lang = "en";
		}
	}

	public static String s(int c) {
		if (loaded) {
			switch(c) {
			case ISOLanguageCode:
				if(values[values.length - 1] != null) {
					return values[values.length - 1];
				}
				return "en-US";
			default:
				if(values[c] != null) return values[c];
			}
			
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
				return "Video quality";
			case SET_Appearance:
				return "Appearance";
			case SET_OtherSettings:
				return "Misc";
			case SET_DownloadDir:
				return "Download directory";
			case SET_InvAPI:
				return "Invidious API Instance";
			case SET_StreamProxy:
				return "URL prefix for playback";
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
			case BTN_SearchVideos:
				return "Search videos";
			case TITLE_Loading:
				return "Loading";
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
			case SET_HTTPProxy:
				return "Video playback";
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
			case BTN_Playlists:
				return "Playlists";
			case CMD_ShowLink:
				return "Show link";
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
			case CMD_Func:
				return "Options";
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
			case CMD_FuncMenu:
				return "Menu";
			case SET_Input:
				return "Input";
			case SET_VirtualKeyboard:
				return "Keyboard";
			case SET_NokiaUI:
				return "NokiaUI (if available)";
			case SET_FullScreenInput:
				return "Fullscreen input";
			case SET_InputLanguages:
				return "Input languages";
			case SET_j2mekeyboardSettings:
				return "j2mekeyboard settings";
			case CMD_Clean:
				return "Clean";
			case CMD_FullEdit:
				return "System input";
			case TXT_DownloadDirWarning:
				return "No download folder selected!";
			case SET_ApiProxy:
				return "Proxy for API";
			case SET_UseApiProxy:
				return "API";
			case BTN_Subscribe:
				return "Subscribe";
			case BTN_Unsubscribe:
				return "Unsubscribe";
			case TITLE_Subscriptions:
				return "Subscriptions";
//			case TITLE_History:
//				return "History";
//			case TITLE_Liked:
//				return "Liked videos";
//			case CMD_Like:
//				return "Like";
//			case CMD_Unlike:
//				return "Unlike";
			case BTN_Videos:
				return "Videos";
			case CMD_ChannelsList:
				return "Channels";
			case SET_ChannelBanners:
				return "Channel banners";
			case SET_SearchSuggestions:
				return "Search suggestions";
			case SET_PowerSaving:
				return "Power saving";
			case SET_ImportSubscriptions:
				return "Import subscriptions";
			case SET_ExportSubscriptions:
				return "Export subscriptions";
			case TITLE_Recommendations:
				return "Recommendations";
			case BTN_OlderVideos:
				return "Older videos";
			case SET_PlaybackProxy:
				return "Playback proxy";
			case SET_UrlPrefix:
				return "URL prefix";
			case SET_Proxy:
				return "Proxy";
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
				return "Качество видео";
			case SET_Appearance:
				return "Внешность";
			case SET_OtherSettings:
				return "Прочие настройки";
			case SET_DownloadDir:
				return "Папка для скачивания";
			case SET_InvAPI:
				return "Invidious API инстанс";
			case SET_StreamProxy:
				return "URL прокси для стриминга";
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
			case BTN_SearchVideos:
				return "Поиск видео";
			case TITLE_Loading:
				return "Загрузка";
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
			case SET_HTTPProxy:
				return "Восп. видео";
			case SET_PreLoadRMS:
				return "Кэш изобр. в RMS";
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
			case BTN_Playlists:
				return "Плейлисты";
			case CMD_ShowLink:
				return "Показать ссылку";
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
			case CMD_Func:
				return "Функции";
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
			case CMD_FuncMenu:
				return "Меню";
			case SET_Input:
				return "Ввод";
			case SET_VirtualKeyboard:
				return "Клавиатура";
			case SET_NokiaUI:
				return "NokiaUI (если имеется)";
			case SET_FullScreenInput:
				return "Полноэкранный ввод";
			case SET_InputLanguages:
				return "Языки ввода";
			case SET_j2mekeyboardSettings:
				return "Настройки j2mekeyboard";
			case CMD_Clean:
				return "Очистить";
			case CMD_FullEdit:
				return "Системный ввод";
			case TXT_DownloadDirWarning:
				return "Папка для загрузки не выбрана!";
			case SET_ApiProxy:
				return "Прокси для API";
			case SET_UseApiProxy:
				return "API";
			case BTN_Subscribe:
				return "Подписаться";
			case BTN_Unsubscribe:
				return "Отписаться";
			case TITLE_Subscriptions:
				return "Подписки";
//			case TITLE_History:
//				return "История";
//			case TITLE_Liked:
//				return "Понравившиеся";
//			case CMD_Like:
//				return "Понравивилось";
//			case CMD_Unlike:
//				return "Разонравилось"; // ??
			case BTN_Videos:
				return "Видео";
			case CMD_ChannelsList:
				return "Каналы";
			case SET_ChannelBanners:
				return "Обложки каналов";
			case SET_SearchSuggestions:
				return "Подсказки поиска";
			case SET_PowerSaving:
				return "Экономия энергии";
			case SET_ImportSubscriptions:
				return "Импорт подписок";
			case SET_ExportSubscriptions:
				return "Экспорт подписок";
			case TITLE_Recommendations:
				return "Рекомендации";
			case BTN_OlderVideos:
				return "Следующие видео";
			case SET_PlaybackProxy:
				return "Прокси для проигрывания";
			case SET_UrlPrefix:
				return "URL префикс";
			case SET_Proxy:
				return "Проксировать";
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
			} else if (i % 10 == 1 && i % 100 != 10) {
				return i + " подписчик";
			} else if (i % 10 >= 5 || i % 10 <= 1) {
				return i + " подписчиков";
			} else {
				return i + " подписчика";
			}
			s = Util.replace(s, ".0 ", " ");
			s += " подписчиков";
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
			} else if (i % 10 == 1 && i % 100 != 10) {
				s += " просмотр";
			} else if (i % 10 >= 5 || i % 10 <= 1) {
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
					int i = Integer.parseInt(s.substring(0, s.indexOf(' ')));
					int i10 = i % 10;
					boolean b = true;
					if (s.indexOf("years ago") != -1) {
						if (i10 == 1 && i % 100 != 10) {
							s = "год";
						} else if (i10 >= 5 || i10 <= 1) {
							s = "лет";
						} else {
							s = "года";
						}
					} else if (s.indexOf("months ago") != -1) {
						if (i10 == 1 && i % 100 != 10) {
							s = "месяц";
						} else if (i10 >= 5 || i10 <= 1) {
							s = "месяцев";
						} else {
							s = "месяца";
						}
					} else if (s.indexOf("weeks ago") != -1) {
						if (i10 == 1 && i % 100 != 10) {
							s = "неделю";
						} else if (i % 10 >= 5 || i10 <= 1) {
							s = "недель";
						} else {
							s = "недели";
						}
					} else if (s.indexOf("days ago") != -1) {
						if (i10 == 1 && i % 100 != 10) {
							s = "день";
						} else if (i10 >= 5 || i10 <= 1) {
							s = "дней";
						} else {
							s = "дня";
						}
					} else if (s.indexOf("hours ago") != -1) {
						if (i10 == 1 && i % 100 != 10) {
							s = "час";
						} else if (i10 >= 5 || i10 <= 1) {
							s = "часов";
						} else {
							s = "часа";
						}
					} else if (s.indexOf("minutes ago") != -1) {
						if (i10 == 1 && i % 100 != 10) {
							s = "минуту";
						} else if (i10 >= 5 || i10 <= 1) {
							s = "минут";
						} else {
							s = "минуты";
						}
					} else {
						s = Util.replace(s, "year ago", "год назад");
						s = Util.replace(s, "month ago", "месяц назад");
						s = Util.replace(s, "week ago", "неделю назад");
						s = Util.replace(s, "day ago", "день назад");
						s = Util.replace(s, "hour ago", "час назад");
						s = Util.replace(s, "minute ago", "минуту назад");
						b = false;
					}
					if(b) s = i + " " + s + " назад";
				} catch (Exception e) {
				}
			}
		}
		return s;
	}

}
