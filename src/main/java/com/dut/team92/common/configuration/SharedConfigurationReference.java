package com.dut.team92.common.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import io.sentry.spring.EnableSentry;

@Configuration
@ComponentScan("com.dut.team92.common")
@EnableSentry(dsn = "https://93d61fe21a50432eac486150f295b5f9@o4504276602454016.ingest.sentry.io/4504276608155648")
public class SharedConfigurationReference {
}
