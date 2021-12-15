import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class Locale implements LocaleConstants {
	
	public static final String locale;
	private static boolean loaded;
	private static int localei;
	private static Hashtable table;
	
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
		locale = s.toLowerCase();
		
		InputStream in = null;
		try {
			in = Locale.class.getResourceAsStream("/jtlng." + s);
		} catch (Exception e) {
		}
		if(in == null) {
			if(!s.equals("en") && (s.equals("ru")
					|| s.equals("uk") || s.equals("be") || s.equals("kk")
					|| s.equals("ua") || s.equals("by") || s.equals("kz"))) {
				localei = 1;
			} else {
				localei = 0;
			}
		} else {
			// TODO
			DataInputStream d = new DataInputStream(in);
			table = new Hashtable();
			try {
				try {
					int i;
					while( (i = d.readShort()) != -1) {
						table.put(new Integer(i), d.readUTF());
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
		}
	}
	
	public static String s(int c) {
		if(loaded) {
			return (String) table.get(new Integer(c));
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
				return "View channel";
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
			return ((int) ((i / 1000000D) * 100) / 100) + "M";
		}
		return "" + i;
	}

	public static String videos(int i) {
		if(i <= 0) return null;
		if(localei == 1) {
			return i + " видео";
		}
		if(i == 1) return i + " video";
		return i + " videos";
	}

}
