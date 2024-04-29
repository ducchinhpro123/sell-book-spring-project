package org.project.sellbook.controller;


import org.project.sellbook.model.Book;
import org.project.sellbook.model.Cart;
import org.project.sellbook.model.CartItem;
import org.project.sellbook.model.User;
import org.project.sellbook.repository.BookRepository;
import org.project.sellbook.repository.CartItemRepository;
import org.project.sellbook.repository.CartRepository;
import org.project.sellbook.service.CartService;
import org.project.sellbook.utils.UserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Controller
public class CartController {

    private final UserUtil userUtil;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;

    public CartController(UserUtil userUtil, CartRepository cartRepository, BookRepository bookRepository, CartService cartService, CartItemRepository cartItemRepository) {
        this.userUtil = userUtil;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
        this.cartItemRepository = cartItemRepository;
    }

    @GetMapping("/cart")
    public String cartView(Model model) {
        User user = userUtil.getCurrentUser();
        Cart cart = user.getCart();
        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setUser(user);

            user.setCart(newCart);
            cartRepository.save(newCart);
        }
        model.addAttribute("cart", cart);
        assert cart != null;
        Set<CartItem> cartItemList = cart.getCartItems();
        model.addAttribute("cartItemList", cartItemList);
        // calculate total price
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartItem cartItem : cartItemList) {
            totalPrice = totalPrice.add(cartItem.getBook().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }
        model.addAttribute("totalPrice", totalPrice);
        return "cart/cart";
    }

    @PostMapping("/cart/add-to-cart")
    public String addToCart(@RequestParam int bookId, @RequestParam int quantity) {

        User user = userUtil.getCurrentUser();
        Book book = bookRepository.findById(bookId).orElseThrow();
        cartService.addToCart(user, book, quantity);

        return "redirect:/cart";
    }

    @GetMapping("/cart/delete")
    public String deleteBook(@RequestParam("cartItemId") int cartItemId, RedirectAttributes redirectAttributes) {
        Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);

        if (cartItem.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Cannot found any books with ID: " + cartItemId);
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        } else {
            cartItemRepository.deleteById(cartItemId);
            redirectAttributes.addFlashAttribute("message", "Deleted successfully.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        }
        return "redirect:/cart";
    }
}
