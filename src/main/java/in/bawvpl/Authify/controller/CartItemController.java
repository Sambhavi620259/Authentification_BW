package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.io.CartItemRequest;
import in.bawvpl.Authify.io.CartItemResponse;
import in.bawvpl.Authify.service.CartItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartItemController {

    private final CartItemService cartService;

    // ✅ Manual constructor (fixes your error)
    public CartItemController(CartItemService cartService) {
        this.cartService = cartService;
    }

    // ✅ Add item
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartItemResponse> addItem(
            @PathVariable String userId,
            @Valid @RequestBody CartItemRequest req) {

        return ResponseEntity.ok(cartService.addItem(userId, req));
    }

    // ✅ Get items
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartItemResponse>> getItems(
            @PathVariable String userId) {

        return ResponseEntity.ok(cartService.getItemsForUser(userId));
    }

    // ✅ Remove item
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable String userId,
            @PathVariable String productId) {

        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
}