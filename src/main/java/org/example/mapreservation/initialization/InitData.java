package org.example.mapreservation.initialization;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@RequiredArgsConstructor
@Component
public class InitData {

    private final DataLoadService dataLoadService;

    @PostConstruct
    public void initData() {
        dataLoadService.load();
    }
}
