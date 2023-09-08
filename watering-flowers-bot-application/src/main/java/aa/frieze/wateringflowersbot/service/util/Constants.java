package aa.frieze.wateringflowersbot.service.util;

import com.vdurmont.emoji.EmojiParser;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy' в 'hh:mm");

    // messages
    public static final String HANDLER_NOT_FOUND_MESSAGE = "Для заданной команды нет обработчика :(";
    public static final String INCORRECT_TG_ACCOUNT_MESSAGE = "Если вы видите это сообщение, значит, что-то пошло " +
            "не так с добавлением этого чата в БД при /start. Пожалуйста, сообщите разработчикам";
    public static final String START_MESSAGE = "Данный бот предназначен для напоминаний пользователю о поливе " +
            "цветов, в память о погибшем кактусе 3-( Так же можно использовать для любых других периодических " +
            "напоминаний";
    public static final String NOTIFYING_MESSAGE = "%s Настало время %s!\nСледующее уведомление придёт %s\n"; // todo
    public static final String NOTIFYING_MESSAGE_END = "\nВы можете отписаться от получения уведомлений в меню " +
            "просмотра уведомлений";

    // emoji
    public static final String STOPWATCH_EMOJI = EmojiParser.parseToUnicode(":stopwatch:");
}
