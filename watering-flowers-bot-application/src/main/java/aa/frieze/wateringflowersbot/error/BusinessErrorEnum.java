package aa.frieze.wateringflowersbot.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Список сообщений об ошибке.
 */
@Getter
@AllArgsConstructor
public enum BusinessErrorEnum {

    L999("999", "Непредвиденная ошибка"),
    E001("001", "Почтовый ящик или номер телефона уже привязан к другому активному пользователю"),
    E002("002", "Пользователь не найден"),
    E003("003", "Не достаточно параметров для добавления пользователя"),
    E100("100", "Сервис недоступен неавторизованным пользователям"),
    E101("101", "Ошибка доступа (типа 403)"),
    E102("102", "Сущность %s с id = %s не найдена"),
    E103("103", "Отправка сообщений в данный момент недоступна"),
    E104("104", "Пользователь с ID %s не найден"),
    E105("105", "Аудиозапись с номером %s и типом %s не найдена");

    private final String code;
    private final String message;

    public AppException thr(Object... args) {
        return new AppException(this, String.format(this.message, args));
    }

    public void thr(Boolean isTrue, Object... args) {
        if (Boolean.TRUE.equals(isTrue)) return;
        throw new AppException(this, String.format(this.message, args));
    }
}
