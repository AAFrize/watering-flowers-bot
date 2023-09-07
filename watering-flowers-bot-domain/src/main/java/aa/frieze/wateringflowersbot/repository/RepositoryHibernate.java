package aa.frieze.wateringflowersbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import aa.frieze.wateringflowersbot.domain.common.AbstractEntity;

@NoRepositoryBean
public interface RepositoryHibernate<T extends AbstractEntity<Long>> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}