package com.skybrisk.bookstore.config;

import com.skybrisk.bookstore.entity.Book;
import com.skybrisk.bookstore.entity.Role;
import com.skybrisk.bookstore.entity.User;
import com.skybrisk.bookstore.repository.BookRepository;
import com.skybrisk.bookstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(BookRepository bookRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize Admin User if not exists
        if (userRepository.findByEmail("admin@skybrisk.com").isEmpty()) {
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@skybrisk.com")
                    .password(passwordEncoder.encode("adminpassword"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user initialized: admin@skybrisk.com / adminpassword");
        }

        // Initialize Sample Books if empty
        if (bookRepository.count() == 0) {
            List<Book> sampleBooks = Arrays.asList(
                    Book.builder()
                            .title("The Great Gatsby")
                            .author("F. Scott Fitzgerald")
                            .genre("Classic")
                            .isbn("9780743273565")
                            .price(new BigDecimal("12.99"))
                            .description("A story of decadence and excess.")
                            .stockQuantity(15)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/81af+MCATTL.jpg")
                            .build(),
                    Book.builder()
                            .title("1984")
                            .author("George Orwell")
                            .genre("Dystopian")
                            .isbn("9780451524935")
                            .price(new BigDecimal("10.50"))
                            .description("A chilling look at a totalitarian future.")
                            .stockQuantity(20)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/71kXa1dqM9L.jpg")
                            .build(),
                    Book.builder()
                            .title("To Kill a Mockingbird")
                            .author("Harper Lee")
                            .genre("Fiction")
                            .isbn("9780061120084")
                            .price(new BigDecimal("14.99"))
                            .description("A novel about racial injustice and the loss of innocence.")
                            .stockQuantity(12)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/81gepf1eMqL.jpg")
                            .build(),
                    Book.builder()
                            .title("The Catcher in the Rye")
                            .author("J.D. Salinger")
                            .genre("Fiction")
                            .isbn("9780316769488")
                            .price(new BigDecimal("9.99"))
                            .description("A teenage boy's journey through New York City.")
                            .stockQuantity(18)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/8125BD7m89L.jpg")
                            .build(),
                    Book.builder()
                            .title("The Hobbit")
                            .author("J.R.R. Tolkien")
                            .genre("Fantasy")
                            .isbn("9780547928227")
                            .price(new BigDecimal("15.99"))
                            .description("The prelude to The Lord of the Rings.")
                            .stockQuantity(25)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/91b0C2YNSrL.jpg")
                            .build(),
                    Book.builder()
                            .title("Brave New World")
                            .author("Aldous Huxley")
                            .genre("Dystopian")
                            .isbn("9780060850524")
                            .price(new BigDecimal("11.25"))
                            .description("A vision of a genetically engineered future.")
                            .stockQuantity(10)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/81z79z8f8pL.jpg")
                            .build(),
                    Book.builder()
                            .title("The Alchemist")
                            .author("Paulo Coelho")
                            .genre("Adventure")
                            .isbn("9780062315007")
                            .price(new BigDecimal("13.50"))
                            .description("A fable about following your dream.")
                            .stockQuantity(30)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/71aFt4+OTOL.jpg")
                            .build(),
                    Book.builder()
                            .title("Sapiens")
                            .author("Yuval Noah Harari")
                            .genre("Non-fiction")
                            .isbn("9780062316097")
                            .price(new BigDecimal("18.99"))
                            .description("A brief history of humankind.")
                            .stockQuantity(22)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/713jIoMO3UL.jpg")
                            .build(),
                    Book.builder()
                            .title("Educated")
                            .author("Tara Westover")
                            .genre("Memoir")
                            .isbn("9780399590504")
                            .price(new BigDecimal("16.00"))
                            .description("A woman's journey from a survivalist family to a PhD.")
                            .stockQuantity(8)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/81WojUxbbFL.jpg")
                            .build(),
                    Book.builder()
                            .title("The Midnight Library")
                            .author("Matt Haig")
                            .genre("Fiction")
                            .isbn("9780525559474")
                            .price(new BigDecimal("14.00"))
                            .description("A library of all the lives you could have lived.")
                            .stockQuantity(14)
                            .imageUrl("https://images-na.ssl-images-amazon.com/images/I/81J6APjwxlL.jpg")
                            .build());
            bookRepository.saveAll(sampleBooks);
            System.out.println("10 Sample books initialized successfully.");
        }
    }
}
