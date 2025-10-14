package mx.uam.ayd.proyecto;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@TestConfiguration
@Profile("test")
@EnableTransactionManagement
public class TestConfig {

} 