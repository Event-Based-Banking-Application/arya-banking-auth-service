package org.arya.banking.auth;

import org.arya.banking.auth.config.OAuth2FeignConfig;
import org.arya.banking.common.config.KafkaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.arya.banking.auth", "org.arya.banking.common"})
@EnableFeignClients(defaultConfiguration = OAuth2FeignConfig.class)
public class AryaBankingAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AryaBankingAuthServiceApplication.class, args);
    }

}
