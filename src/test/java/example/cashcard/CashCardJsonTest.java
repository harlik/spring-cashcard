package example.cashcard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = new CashCard[]{
                new CashCard(123L, 456.78),
                new CashCard(124L, 789.01),
                new CashCard(125L, 234.56),
                new CashCard(126L, 987.10)
        };
    }

    @Test
    public void CashCardListDeserializationTest() throws IOException {
        String expected = """
                [
                  {
                    "id": 123,
                    "amount": 456.78
                  },
                  {
                    "id": 124,
                    "amount": 789.01
                  },
                  {
                    "id": 125,
                    "amount": 234.56
                  },
                  {
                    "id": 126,
                    "amount": 987.10
                  }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }

    @Test
    public void CashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards))
                .isStrictlyEqualToJson("list.json");
//        assertThat(json.write(cashCard))
//                .hasJsonPathNumberValue("@.id");
//        assertThat(json.write(cashCard))
//                .extractingJsonPathNumberValue("@.id")
//                .isEqualTo(99);
//        assertThat(json.write(cashCard))
//                .hasJsonPathNumberValue("@.amount");
//        assertThat(json.write(cashCard))
//                .extractingJsonPathNumberValue("@.amount")
//                .isEqualTo(123.45);
    }

    @Test
    public void CashCardDeserializationTest() throws IOException {
        String expected = """
                {
                  "amount": 123.45,
                  "id": 99
                }
                """;
        assertThat(json.parse(expected))
                .isEqualTo(new CashCard(99L, 123.45));
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    @Test
    public void CashCardSerializationTest() throws IOException {
        CashCard cashCard = new CashCard(99L, 123.45);
        assertThat(json.write(cashCard))
                .isStrictlyEqualToJson("single.json");
        assertThat(json.write(cashCard))
                .hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard))
                .extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(json.write(cashCard))
                .hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard))
                .extractingJsonPathNumberValue("@.amount")
                .isEqualTo(123.45);
    }
}
