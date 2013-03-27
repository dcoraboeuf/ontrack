package net.ontrack.service.api;

import org.springframework.scheduling.Trigger;

public interface ScheduledService {

    Runnable getTask();

    Trigger getTrigger();

}
