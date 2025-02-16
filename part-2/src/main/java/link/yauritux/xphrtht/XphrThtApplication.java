package link.yauritux.xphrtht;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Time Tracking API",
                version = "0.1",
                description = "Employee Time Tracking (Work Hours) Report API",
                contact = @Contact(name = "M. Yauri M. Attamimi", email = "yaurignell@gmail.com")))
@SpringBootApplication
public class XphrThtApplication {

    public static void main(String[] args) {
        SpringApplication.run(XphrThtApplication.class, args);
    }

}
