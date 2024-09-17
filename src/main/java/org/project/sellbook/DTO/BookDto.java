package org.project.sellbook.DTO;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.project.sellbook.model.Author;
import org.project.sellbook.model.Category;
import org.project.sellbook.model.Order;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class BookDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private MultipartFile image;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "book")
    private Set<Order> orders = new LinkedHashSet<>();

   	public Integer getId() {
   		return id;
   	}
   
   	public void setId(Integer id) {
   		this.id = id;
   	}
   
   	public String getTitle() {
   		return title;
   	}
   
   	public void setTitle(String title) {
   		this.title = title;
   	}
   
   	public Author getAuthor() {
   		return author;
   	}
   
   	public void setAuthor(Author author) {
   		this.author = author;
   	}
   
   	public Category getCategory() {
   		return category;
   	}
   
   	public void setCategory(Category category) {
   		this.category = category;
   	}
   
   	public BigDecimal getPrice() {
   		return price;
   	}
   
   	public void setPrice(BigDecimal price) {
   		this.price = price;
   	}
   
   	public String getDescription() {
   		return description;
   	}
   
   	public void setDescription(String description) {
   		this.description = description;
   	}
   
   	public MultipartFile getImage() {
   		return image;
   	}
   
   	public void setImage(MultipartFile image) {
   		this.image = image;
   	}
   
   	public Integer getStock() {
   		return stock;
   	}
   
   	public void setStock(Integer stock) {
   		this.stock = stock;
   	}
   
   	public Set<Order> getOrders() {
   		return orders;
   	}
   
   	public void setOrders(Set<Order> orders) {
   		this.orders = orders;
   	}



}
