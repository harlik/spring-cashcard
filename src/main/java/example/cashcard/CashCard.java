package example.cashcard;

import org.springframework.data.annotation.Id;

public record CashCard(@Id Long id, Double amount, String owner) {
}
//import lombok.AllArgsConstructor;
//
//@AllArgsConstructor
//public class CashCard {
//    private long field1;
//    private double field2;
//}
