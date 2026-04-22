package com.hmis;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@OpenAPIDefinition(
    info = @Info(
        title = "HMIS Core API",
        version = "1.0.0",
        description = "Hệ thống Quản lý Thông tin Bệnh viện (HMIS) - " +
                      "Module Bệnh án điện tử (EHR) & Quản lý Trạm Y tế",
        contact = @Contact(name = "HMIS Team", email = "dev@hmis.local"),
        license = @License(name = "Proprietary", url = "https://hmis.local")
    )
)
public class HmisApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmisApplication.class, args);
    }
}
