package aa.frieze.wateringflowersbot.domain.common;

import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@MappedSuperclass
@FieldNameConstants
public abstract class AbstractEntity<V> implements IdProjection<V> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    private V id;

    public V getId() {
        return id;
    }

    public void setId(V id) {
        this.id = id;
    }
}