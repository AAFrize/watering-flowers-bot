package aa.frieze.wateringflowersbot.service.util;

import com.vdurmont.emoji.EmojiParser;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Constants {

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy' в 'hh:mm");

    // emoji
    public static final String STOPWATCH_EMOJI = EmojiParser.parseToUnicode(":stopwatch:");
    public static final String PENSIVE_EMOJI = EmojiParser.parseToUnicode(":pensive:");
    public static final String HMM_EMOJI = EmojiParser.parseToUnicode(":hmm:");

    // messages
    public static final String START_MESSAGE = "Данный бот предназначен для напоминаний пользователю о поливе " +
            "цветов, в память о погибшем кактусе " + PENSIVE_EMOJI + " Так же можно использовать для любых " +
            "других периодических напоминаний";
    public static final String TRY_AGAIN_MESSAGE = "Не получилось обработать запрос" + PENSIVE_EMOJI
            + " Пожалуйста, попробуйте повторить с начала";
    public static final String HANDLER_NOT_FOUND_MESSAGE = "Для заданной команды нет обработчика " + HMM_EMOJI;
    public static final String INCORRECT_TG_ACCOUNT_MESSAGE = "Если вы видите это сообщение, значит, что-то пошло " +
            "не так с добавлением этого чата в БД при /start. Пожалуйста, сообщите разработчикам";
    public static final String TITLE_WARNING_MESSAGE = "Название напоминания не должно совпадать с уже существующими.";
    public static final String TITLE_WAITING_MESSAGE = "Введите название напоминания. " + TITLE_WARNING_MESSAGE;
    public static final String NOTIFYING_MESSAGE = "%s Настало время %s!\nСледующее уведомление придёт %s\n";
    public static final String NOTIFYING_MESSAGE_END = "\nВы можете отписаться от получения уведомлений в меню " +
            "просмотра уведомлений";
    public static final String NOTIFICATION_INFO = """
            *%s*:
            Статус: %s
            Время последнего напоминания: %s
            Время запланированного напоминания: %s""";

    public static final String ACTUAL_NOTIFICATION_INFO = """
            *%s*:
            Время последнего напоминания: %s
            Время запланированного напоминания: %s""";

}

