package aa.frieze.wateringflowersbot.service.util;

import com.vdurmont.emoji.EmojiParser;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Constants {

    public static final String dateFormatterInputString = "dd.MM.yyyy' 'HH:mm";
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy' в 'HH:mm");
    public static final DateTimeFormatter dateFormatterInput = DateTimeFormatter.ofPattern(dateFormatterInputString);

    // emoji
    public static final String STOPWATCH_EMOJI = EmojiParser.parseToUnicode(":stopwatch:");
    public static final String PENSIVE_EMOJI = EmojiParser.parseToUnicode(":pensive:");
    public static final String HMM_EMOJI = EmojiParser.parseToUnicode(":hmm:");
    public static final String MEMO_EMOJI = EmojiParser.parseToUnicode(":memo:");
    public static final String ALARM_CLOCK_EMOJI = EmojiParser.parseToUnicode(":alarm_clock:");
    public static final String ARROW_FORWARD_EMOJI = EmojiParser.parseToUnicode(":arrow_forward:");
    public static final String STOP_BUTTON_EMOJI = EmojiParser.parseToUnicode("⏹");

    // buttons
    public static final String SUBSCRIBE_BUTTON = "Создать новое уведомление";
    public static final String UNSUBSCRIBE_BUTTON = "Отписаться от рассылки уведомлений";
    public static final String VIEW_INFO_BUTTON = "Просмотр уведомлений";
    public static final String CHANGE_TIMEZONE_BUTTON = "Изменить таймзону";
    public static final String UNSUBSCRIBE_ALL_BUTTON = "Отписаться от всех уведомлений";
    public static final String UNSUBSCRIBE_CUSTOM_BUTTON = "Выбрать уведомления для отписки";
    public static final String UNSUBSCRIBE_ONE_BUTTON = "Отписаться";
    public static final String VIEW_ACTUAL_INFO_BUTTON = "Просмотр активных уведомлений";
    public static final String VIEW_ALL_INFO_BUTTON = "Просмотр всех уведомлений";
    public static final String CURRENT_DATE_BUTTON = "Выбрать текущие дату и время, как начало периода";
    public static final String CUSTOM_DATE_BUTTON = "Ввести другие дату и время, как начало периода";

    // messages
    public static final String START_MESSAGE = "Данный бот предназначен для напоминаний пользователю о поливе " +
                                               "цветов, в память о погибшем кактусе " + PENSIVE_EMOJI + " Так же можно использовать для любых " +
                                               "других периодических напоминаний";
    public static final String MAIN_MENU_MESSAGE = "Воспользуйтесь главным меню";
    public static final String UNSUBSCRIPTION_MENU_MESSAGE = "От каких уведомлений вы хотите отписаться?";
    public static final String VIEW_MENU_MESSAGE = "Информацию о каких уведомлениях вы хотите получить?";

    public static final String TRY_AGAIN_MESSAGE = "Не получилось обработать запрос" + PENSIVE_EMOJI
                                                   + " Пожалуйста, попробуйте повторить с начала";
    public static final String HANDLER_NOT_FOUND_MESSAGE = "Для заданной команды нет обработчика " + HMM_EMOJI;
    public static final String INCORRECT_TG_ACCOUNT_MESSAGE = "Если вы видите это сообщение, значит, что-то пошло " +
                                                              "не так с добавлением этого чата в БД при /start. Пожалуйста, сообщите разработчикам";

    public static final String TITLE_WARNING_MESSAGE = "Название напоминания не должно совпадать с уже существующими.";
    public static final String TITLE_WAITING_MESSAGE = "Введите название напоминания. " + TITLE_WARNING_MESSAGE;
    public static final String TIMEZONE_WAITING_MESSAGE = "Введите таймзону, в соответствии с которой хотите получать " +
                                                          "уведомления. Примеры: \"+03:00\", \"GMT+03\", \"EAT\"";
    public static final String TIMEZONE_WARNING_MESSAGE = "Таймзона введена некорректно. " +
                                                          "Пожалуйста, попробуйте ещё раз. Примеры: \"+03:00\", \"GMT+03\", \"EAT\"";
    public static final String START_DATE_CHOOSING_MESSAGE = "Выберите дату начала напоминаний";
    public static final String START_DATE_CUSTOM_MESSAGE = "Введите дату начала напоминаний в формате "
                                                           + dateFormatterInputString + "\nНапример: \"10.09.2025 12:00\"";
    public static final String START_DATE_WARNING_MESSAGE = "Некорректный формат даты. " + START_DATE_CUSTOM_MESSAGE;
    public static final String START_DATE_ILLEGAL_WARNING_MESSAGE = "Дата первого напоминания не должна быть " +
                                                                    "раньше текущей даты";
    public static final String DURATION_CUSTOM_MESSAGE = """
            Ожидаемый формат: число + единица измерения.
            Примеры: "1d", "1 день", "2нед.", "2нед.", "3 г."
            __Примечание:"м" и "М" используются для обозначения минут, для месяца следует ввести "мес"__
            """;
    public static final String DURATION_WAITING_MESSAGE = "Введите период напоминаний.\n" + DURATION_CUSTOM_MESSAGE;
    public static final String DURATION_WARNING_MESSAGE = "Некорректный формат." + DURATION_CUSTOM_MESSAGE;
    public static final String NOTIFYING_MESSAGE = "%s Настало время %s!\nСледующее уведомление придёт %s\n";
    public static final String NOTIFYING_MESSAGE_END = "\nВы можете отписаться от получения уведомлений в главном меню";

    public static final String ACTUAL_NOTIFICATIONS_NOT_FOUND_MESSAGE = "Активных подписок не найдено";
    public static final String NOTIFICATIONS_NOT_FOUND_MESSAGE = "Подписок не найдено";
    public static final String UNSUBSCRIBE_SUCCESS_MESSAGE = "Вы успешно отписались от всех подписок";
    public static final String UNSUBSCRIBE_SUCCESS_CUSTOM_MESSAGE = "Вы успешно отписались от уведомления *%s*";

    public static final String NOTIFICATION_INFO = MEMO_EMOJI + " *%s*:\n" + "%s Статус: %s\n" + STOPWATCH_EMOJI +
                                                   " Время последнего напоминания: %s\n" + ALARM_CLOCK_EMOJI +
                                                   " Время запланированного напоминания: %s";

    public static final String ACTUAL_NOTIFICATION_INFO = MEMO_EMOJI + " *%s*:\n" + STOPWATCH_EMOJI +
                                                          " Время последнего напоминания: %s\n" + ALARM_CLOCK_EMOJI +
                                                          " Время запланированного напоминания: %s";

    public static final String NEW_NOTIFICATION_INFO = "Уведомление успешно создано:\n" + ACTUAL_NOTIFICATION_INFO;

}

