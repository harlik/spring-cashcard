package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.ListAssert.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CashCardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards/123", String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        assertThat((Number) documentContext.read("$.id"))
                .isEqualTo(123);
    }

    @Test
    @DirtiesContext
    void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(null, 250d);
        ResponseEntity<Void> createResponse =
                restTemplate.postForEntity("/cashcards", newCashCard, Void.class);

        assertThat(createResponse.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity <String> response =
                restTemplate.getForEntity(locationOfNewCashCard, String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        assertThat((Number) documentContext.read("$.amount"))
                .isEqualTo(250d);
        assertThat((Number) documentContext.read("$.id"))
                .isNotNull();

    }

    @Test
    void shouldNotReturnACashCardWithUnknownId() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards/1000", String.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .isBlank();
    }

    @Test
    void shouldReturnAllCashCards() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        var documentContext = JsonPath.parse(response.getBody());
        int numberOfCashCards = documentContext.read("$.length()");
        assertThat(numberOfCashCards).isEqualTo(4);
        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(123, 124, 125, 126);
        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(456.78, 789.01, 234.56, 987.10);
    }

    @Test
    void shouldReturnCashCardsPage() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards?page=0&size=1", String.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        var documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnLargestAmount() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        var documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(1);
        double largestAmount = documentContext.read("$[0].amount");
        assertThat(largestAmount).isEqualTo(987.10);
    }

    @Test
    void shouldReturnSmallestAmount() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards?page=0&size=1&sort=amount,asc", String.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        var documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(1);
        double largestAmount = documentContext.read("$[0].amount");
        assertThat(largestAmount).isEqualTo(234.56);
    }

    @Test
    void shouldReturnDefaultPageWhenNoParametersPassed() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        var documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(4);
        double largestAmount = documentContext.read("$[0].amount");
        assertThat(largestAmount).isEqualTo(987.10);
        double smallestAmount = documentContext.read("$[3].amount");
        assertThat(smallestAmount).isEqualTo(234.56);
    }
}
