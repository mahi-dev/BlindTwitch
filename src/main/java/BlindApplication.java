import com.github.twitch4j.TwitchClientBuilder;
import configuration.BlindConfiguration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import repository.GameRepository;
import repository.GameResponseRepository;
import service.*;
import utils.storage.FileSystemStorage;

import java.io.IOException;
import java.nio.file.Path;

@SpringBootApplication
@ImportAutoConfiguration({
        ServletWebServerFactoryAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        WebClientAutoConfiguration.class,
        PropertyPlaceholderAutoConfiguration.class,
        MessageSourceAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        ThymeleafAutoConfiguration.class
})
@Import(BlindApplication.DevToolsConfiguration.class)
@EnableJpaRepositories({"repository"})
@EnableTransactionManagement
@ConfigurationPropertiesScan({"configuration"})
@ComponentScan({"controller","model"})
public class BlindApplication {

    @Configuration
    @ConditionalOnClass(name = "org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration")
    @ImportAutoConfiguration(LocalDevToolsAutoConfiguration.class)
    public static class DevToolsConfiguration {
    }

    @Getter
    @Value("${fileSystemStorage.basePath}")
    private Path fileSystemStorageBasePath;

    @Lazy
    @Bean
    public FileSystemStorage fileSystemStorage() throws IOException {
        return new FileSystemStorage(this.fileSystemStorageBasePath);
    }

    @Lazy
    @Bean
    public ServiceClient.GameService gameService(GameRepository repository) throws IOException {
        return new GameService(repository);
    }

    @Lazy
    @Bean
    public ServiceClient.ImportExportService importExportService(ServiceClient.GameService gameService,
                                                                 FileSystemStorage fileSystemStorage)
            throws IOException {
        return new ImportExportService(gameService,fileSystemStorage);
    }

    @Lazy
    @Bean
    public ServiceClient.ResponseService responseService(GameResponseRepository repository) throws IOException {
        return new ResponseService(repository);
    }

    @Bean
    @Lazy
    public TwitchClientBuilder  clientBuilder(){
        return TwitchClientBuilder.builder();
    }

    @Bean
    @Lazy
    public ServiceClient.TwitchService authenticateTwitchClient(BlindConfiguration blindConfiguration, TwitchClientBuilder clientBuilder){
        return new AuthenticateTwitchClient( blindConfiguration, clientBuilder);
    }

    public static void main(final String[] args) {
        SpringApplication.run(BlindApplication.class, args);
    }
}
