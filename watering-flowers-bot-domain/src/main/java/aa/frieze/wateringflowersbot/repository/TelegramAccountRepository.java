package aa.frieze.wateringflowersbot.repository;

import aa.frieze.wateringflowersbot.domain.TelegramAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramAccountRepository extends RepositoryHibernate<TelegramAccount> {

}
