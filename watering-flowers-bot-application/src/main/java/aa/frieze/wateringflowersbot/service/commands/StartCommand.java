package aa.frieze.wateringflowersbot.service.commands;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import aa.frieze.wateringflowersbot.repository.TelegramAccountRepository;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Команда "Старт"
 */
public class StartCommand extends ServiceCommand {

    private final TelegramAccountRepository telegramAccountRepository;
    private final String startMessage;

    public StartCommand(String identifier, String description, TelegramAccountRepository telegramAccountRepository,
                        String startMessage) {
        super(identifier, description);
        this.telegramAccountRepository = telegramAccountRepository;
        this.startMessage = startMessage;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        //формируем имя пользователя - поскольку userName может быть не заполнено, для этого случая используем имя и фамилию пользователя
        String userName = (user.getUserName() != null) ? user.getUserName() :
            String.format("%s %s", user.getLastName(), user.getFirstName());
        String username = (user.getUserName() != null) ? user.getUserName() : String.valueOf(user.getId());
        Long chatId = chat.getId();
        if (telegramAccountRepository.findByChatId(chatId).isEmpty()) {
            telegramAccountRepository.save(createNewAccount(chatId, username));
        }
        //обращаемся к методу суперкласса для отправки пользователю ответа
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, startMessage, true);
    }

    private TelegramAccount createNewAccount(Long chatId, String username) {
        TelegramAccount account = new TelegramAccount();
        account.setChatId(chatId);
        account.setUsername(username);
        return account;
    }
}
