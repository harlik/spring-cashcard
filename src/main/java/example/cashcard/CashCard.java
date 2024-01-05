package example.cashcard;

import org.springframework.data.annotation.Id;

public record CashCard(@Id Long id, Double amount) {
}
//import lombok.AllArgsConstructor;
//
//@AllArgsConstructor
//public class CashCard {
//    private long field1;
//    private double field2;
//}
