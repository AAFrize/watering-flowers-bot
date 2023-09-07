package aa.frieze.wateringflowersbot.config;

import aa.frieze.wateringflowersbot.service.util.Utils;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JpaConfig extends CamelCaseToUnderscoresNamingStrategy {

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbc) {
        String tableName = Utils.camel2underscore(name.getText() + "s");
        return Identifier.toIdentifier(tableName, name.isQuoted());
    }

}
