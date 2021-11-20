import java.io.InputStream;

public class Locale implements LocaleConstants {
	
	public static final String locale;
	private static boolean loaded;
	private static int localei;
	
	static {
		// J9, JRE language property
		String s = System.getProperty("user.language");
		if(s == null) {
			s = System.getProperty("microedition.locale");
			if(s != null && s.length() >= 2) {
			s = s.substring(0, 2);
			} else {
				s = "en";
			}
		}
		locale = s.toLowerCase();
		InputStream in = Locale.class.getResourceAsStream("/l." + s);
		if(in == null) {
			if(s.startsWith("ru") || s.startsWith("kz") || s.startsWith("by") || s.startsWith("ua")) {
				localei = 1;
			} else {
				localei = 0;
			}
		}
	}
	
	public static String s(int c) {
		if(loaded) {
			return null;
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
				return "Trends";
			case TITLE_Popular:
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
		return null;
	}

}
