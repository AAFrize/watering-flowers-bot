package aa.frieze.wateringflowersbot.repository;

import aa.frieze.wateringflowersbot.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends RepositoryHibernate<Notification> {

    Optional<Notification> findByTitleAndTelegramAccountId(String title, Long telegramAccountId);
}
