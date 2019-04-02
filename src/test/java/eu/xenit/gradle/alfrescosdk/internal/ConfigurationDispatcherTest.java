package eu.xenit.gradle.alfrescosdk.internal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.gradle.api.Action;
import org.junit.Test;

public class ConfigurationDispatcherTest {

    private static class CallsCollector implements Action<String> {
        private List<String> configurations = new ArrayList<>();

        @Override
        public void execute(String configuration) {
            configurations.add(configuration);
        }

        public List<String> getConfigurations() {
            return configurations;
        }
    }

    @Test
    public void configurationFirst() {
        ConfigurationDispatcher<String> dispatcher = new ConfigurationDispatcher<>();

        dispatcher.add("abc");
        dispatcher.add("xyz");

        CallsCollector callsCollector = new CallsCollector();
        dispatcher.add(callsCollector);

        assertEquals(Arrays.asList("abc", "xyz"), callsCollector.getConfigurations());
    }


    @Test
    public void actionFirst() {
        ConfigurationDispatcher<String> dispatcher = new ConfigurationDispatcher<>();

        CallsCollector callsCollector = new CallsCollector();
        dispatcher.add(callsCollector);

        assertEquals(Collections.emptyList(), callsCollector.getConfigurations());

        dispatcher.add("abc");
        dispatcher.add("xyz");

        assertEquals(Arrays.asList("abc", "xyz"), callsCollector.getConfigurations());
    }

    @Test
    public void interleaved() {
        ConfigurationDispatcher<String> dispatcher = new ConfigurationDispatcher<>();

        dispatcher.add("abc");

        CallsCollector callsCollector = new CallsCollector();
        dispatcher.add(callsCollector);

        assertEquals(Collections.singletonList("abc"), callsCollector.getConfigurations());

        dispatcher.add("xyz");

        assertEquals(Arrays.asList("abc", "xyz"), callsCollector.getConfigurations());

    }

}
