package eu.xenit.gradle.alfrescosdk.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.gradle.api.Action;

public class ConfigurationDispatcher<T> {
    private final List<Action<? super T>> actions = new LinkedList<>();

    private final List<T> configurations = new ArrayList<>();

    public void add(T configuration) {
        configurations.add(configuration);
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
