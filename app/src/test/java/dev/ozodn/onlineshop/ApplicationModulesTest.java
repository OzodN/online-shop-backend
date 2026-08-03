package dev.ozodn.onlineshop;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ApplicationModulesTest {

    @Test
    void verifyModulithStructure() {
        ApplicationModules modules = ApplicationModules.of(OnlineShopApplication.class);

        modules.forEach(System.out::println);

        modules.verify();
    }
}
