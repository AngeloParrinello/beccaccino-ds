package it.unibo.sd.beccacino;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class LogTest {
    @Test
    void testCreateDBManager() {
        assertDoesNotThrow(() -> {
            LoggerFactory.getLogger("ds-app").info("PROVAPROVAPROVA");
            sleep(10000);
        });
    }
}
