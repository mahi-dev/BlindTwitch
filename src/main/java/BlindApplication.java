import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import configuration.BlindConfiguration;
import lombok.Getter;
import lombok.NonNull;
import message.MessageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import repository.ChannelUserRepository;
import repository.GameRepository;
import repository.GameResponseRepository;
import repository.SettingRepository;
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
@ComponentScan({"controller","context"})
@EntityScan({"model"})
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
    public CommonsMultipartResolver filterMultipartResolver(){
        return new  CommonsMultipartResolver();
    }

    @Lazy
    @Bean
    public FileSystemStorage fileSystemStorage() throws IOException {
        return new FileSystemStorage(this.fileSystemStorageBasePath);
    }

    @Lazy
    @Bean
    public ServiceClient.UserService userService(@NonNull ChannelUserRepository repository) throws IOException {
        return new ChannelUserService(repository);
    }


    @Lazy
    @Bean
    public ServiceClient.GameService gameService(@NonNull GameRepository repository) throws IOException {
        return new GameService(repository);
    }

    @Lazy
    @Bean
    public ServiceClient.SettingsService settingsService(@NonNull SettingRepository repository) {
        return new SettingService(repository);
    }

    @Lazy
    @Bean
    public ServiceClient.ScoreService scoreService(@NonNull ServiceClient.UserService userService) {
        return new ScoreManager( userService);
    }

    @Lazy
    @Bean
    public ServiceClient.ImportExportService importExportService(@NonNull ServiceClient.GameService gameService,
                                                                 @NonNull ServiceClient.SettingsService settingsService,
                                                                 @NonNull FileSystemStorage fileSystemStorage)
            throws IOException {
        return new ImportExportService(gameService, settingsService, fileSystemStorage);
    }

    @Lazy
    @Bean
    public ServiceClient.ResponseService responseService(@NonNull GameResponseRepository repository) throws IOException {
        return new ResponseService(repository);
    }

    @Lazy
    @Bean
    public TwitchIdentityProvider twitchIdentityProvider(@NonNull BlindConfiguration config){
        return new TwitchIdentityProvider(config.clientId(), config.clientSecret(), config.redirectUrl());
    }

    @Bean
    @Lazy
    public TwitchClientBuilder  clientBuilder(){
        return TwitchClientBuilder.builder();
    }

    @Bean
    @Lazy
    public ServiceClient.TwitchService authenticateTwitchClient(@NonNull BlindConfiguration config,
                                                                @NonNull TwitchIdentityProvider twitchIdentityProvider,
                                                                @NonNull TwitchClientBuilder clientBuilder,
                                                                @NonNull ServiceClient.UserService userService){
        return new AuthenticateTwitchClient(config, twitchIdentityProvider, clientBuilder, userService);
    }

    @Lazy
    @Bean
    public ServiceClient.MessageService messageManager(@NonNull ServiceClient.GameService gameService,
                                                       @NonNull ServiceClient.ResponseService responseService,
                                                       @NonNull ServiceClient.UserService userService) {
        return new MessageManager(gameService, responseService, userService);
    }

    @Lazy
    @Bean
    public ServiceClient.AvatarService avatarService(@NonNull ServiceClient.UserService userService) {
        return new UserAvatarService(userService);
    }

    public static void main(final String[] args) {
        SpringApplication.run(BlindApplication.class, args);
    }
}
