package eu.xenit.gradle.alfrescosdk.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.gradle.api.Action;

public class ConfigurationDispatcher<T> {
    private final List<Action<? super T>> actions = new LinkedList<>();

    private final Set<T> configurations = new LinkedHashSet<>();

    public void add(T configuration) {
        if(!configurations.add(configuration)) {
            // Configuration was already added earlier
            return;
        }
        for (Action<? super T> action : actions) {
            action.execute(configuration);
        }
    }

    public void add(Action<? super T> action) {
        actions.add(action);
        for (T configuration : configurations) {
            action.execute(configuration);
        }
    }

}
