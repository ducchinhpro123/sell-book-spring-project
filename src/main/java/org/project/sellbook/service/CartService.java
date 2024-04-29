package org.project.sellbook.service;

import org.project.sellbook.model.Book;
import org.project.sellbook.model.Cart;
import org.project.sellbook.model.CartItem;
import org.project.sellbook.model.User;
import org.project.sellbook.repository.CartRepository;
import org.springframework.stereotype.Service;


@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void addToCart(User user, Book book, int quantity) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
        }
        user.setCart(cart);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);

        cartItem.setBook(book);
        cartItem.setQuantity(quantity);

        /*
         * check if the book is already in the cart
         *  + if it is, increase the quantity
         *  + if it is not, add the book to the cart
         * */
        if (cart.getCartItems().stream().anyMatch(item -> item.getBook().equals(book))) { // find book in cart
            cart.getCartItems().stream() // if there is a book in the cart
                    .filter(item -> item.getBook().equals(book))// filter out that book
                    .findFirst() // find the first book
                    .ifPresent(item -> item.setQuantity(item.getQuantity() + quantity)); // if the book is found, increase the quantity
            cartRepository.save(cart);
            return;
        }

        cart.getCartItems().add(cartItem);
        cart.setUser(user);

        cartRepository.save(cart);
    }

    public void removeFromCart(User user, Book book) {
        Cart cart = user.getCart();
        if (cart == null) {
            return;
        }
        cart.getCartItems().removeIf(cartItem -> cartItem.getBook().equals(book));
        cartRepository.save(cart);
    }
}
