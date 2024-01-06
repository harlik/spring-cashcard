package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        CashCard cashCard = cashCardRepository
                .findByIdAndOwner(requestedId, principal.getName());
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCard,
                                               UriComponentsBuilder ucb,
                                               Principal principal) {
        CashCard savedCashCard = cashCardRepository.save(
                new CashCard(null, newCashCard.amount(), principal.getName())
        );
        URI savedCashCardLocation = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(savedCashCardLocation).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<CashCard> update(@RequestBody CashCard updatedCashCard,
                                           @PathVariable Long requestedId,
                                           Principal principal) {
        CashCard cashCard = cashCardRepository
                .findByIdAndOwner(requestedId, principal.getName());
        if (cashCard != null) {
            cashCardRepository.save(
                    new CashCard(cashCard.id(), updatedCashCard.amount(), principal.getName())
            );
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
