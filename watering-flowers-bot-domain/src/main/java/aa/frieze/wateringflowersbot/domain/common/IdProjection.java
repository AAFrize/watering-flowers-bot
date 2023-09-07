package aa.frieze.wateringflowersbot.domain.common;

public interface IdProjection<T> {
    T getId();

    void setId(T id);
}
