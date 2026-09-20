package vn.iotstar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import vn.iotstar.config.StorageProperties;
import vn.iotstar.service.IStorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class SpringbootAjaxCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootAjaxCrudApplication.class, args);
    }

    @Bean
    CommandLineRunner initStorage(IStorageService storageService) {
        return args -> storageService.init();
    }
}
