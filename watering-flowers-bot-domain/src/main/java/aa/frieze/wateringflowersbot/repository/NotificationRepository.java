package aa.frieze.wateringflowersbot.repository;

import aa.frieze.wateringflowersbot.domain.Notification;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends RepositoryHibernate<Notification> {
}
