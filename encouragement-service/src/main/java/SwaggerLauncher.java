

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Component  // این انوتیشن باعث می‌شود که Spring Boot این کلاس را به عنوان یک کامپوننت شناسایی کند
public class SwaggerLauncher implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        String swaggerUrl = "http://localhost:8088/swagger-ui/index.html";  // URL Swagger UI
        if (Desktop.isDesktopSupported()) {
            // اگر پشتیبانی از Desktop موجود باشد، آدرس Swagger را در مرورگر باز می‌کند
            Desktop.getDesktop().browse(new URI(swaggerUrl));
        } else {
            // اگر Desktop پشتیبانی نشود، آدرس را در کنسول چاپ می‌کند
            System.out.println("Swagger UI available at: " + swaggerUrl);
        }
    }
}
