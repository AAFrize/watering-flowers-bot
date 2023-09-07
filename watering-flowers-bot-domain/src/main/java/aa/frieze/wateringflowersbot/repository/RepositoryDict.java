package aa.frieze.wateringflowersbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import aa.frieze.wateringflowersbot.domain.common.RefEntity;

/**
 * Интерфейс репозиториев справочников JPA.
 *
 * @param <T>
 */
@NoRepositoryBean
public interface RepositoryDict<T extends RefEntity>
        extends JpaRepository<T, String>, JpaSpecificationExecutor<T> {

    T findByKey(String key);
}
