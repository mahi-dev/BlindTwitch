package service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.Game;
import model.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.SettingRepository;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SettingService implements ServiceClient.SettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(SettingService.class);

    private final SettingRepository repository;

    @Override
    public Set<Setting> getAllSettings() {
        return repository.findAll().stream().collect(Collectors.toSet());
    }

    @Override
    public Set<Setting> getSettings(@NonNull String id) {
        return repository.findById(Long.valueOf(id)).stream().collect(Collectors.toSet());
    }

    @Override
    public Set<Setting> getSettings(@NonNull Game game) {
        return game.getSettings();
    }

    @Override
    @Transactional
    public void storeSetting(@NonNull Setting setting) {
        repository.saveAndFlush(setting);
    }
}