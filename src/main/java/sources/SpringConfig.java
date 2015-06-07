package sources;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
* Spring Java configuration class. See http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java
*
* @author Jan
*/
@Configuration  //je to konfigurace pro Spring
@EnableTransactionManagement //bude řídit transakce u metod označených @Transactional
public class SpringConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .addScript("classpath:schema-database.sql")
                .build();
    }

    @Bean //potřeba pro @EnableTransactionManagement
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean //náš manager, bude obalen řízením transakcí
    public RoomManager roomManager() {
        return new RoomManagerImpl(dataSource());
    }

    @Bean
    public GuestManager guestManager() {
        return new GuestManagerImpl(dataSource());
    }

}
