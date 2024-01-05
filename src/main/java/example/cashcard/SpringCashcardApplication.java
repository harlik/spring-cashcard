package example.cashcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SpringCashcardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCashcardApplication.class, args);
    }

}
