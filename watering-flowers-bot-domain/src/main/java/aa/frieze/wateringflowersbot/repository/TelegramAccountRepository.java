package aa.frieze.wateringflowersbot.repository;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramAccountRepository extends RepositoryHibernate<TelegramAccount> {

    Optional<TelegramAccount> findByChatId(@NotNull Long chatId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "notifications")
    List<TelegramAccount> findAllBySubscribedTrue();
}
