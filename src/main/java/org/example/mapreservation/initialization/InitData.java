package org.example.mapreservation.initialization;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InitData {

    private final DataLoadService dataLoadService;

    @PostConstruct
    public void initData() {
        dataLoadService.load();
    }
}
