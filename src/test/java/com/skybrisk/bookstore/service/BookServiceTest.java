package com.skybrisk.bookstore.service;

import com.skybrisk.bookstore.dto.BookDTO;
import com.skybrisk.bookstore.entity.Book;
import com.skybrisk.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBookById_Success() {
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .price(BigDecimal.valueOf(19.99))
                .stockQuantity(10)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDTO result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void testCreateBook_Success() {
        BookDTO dto = BookDTO.builder()
                .title("New Book")
                .author("New Author")
                .price(BigDecimal.valueOf(25.00))
                .stockQuantity(5)
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("New Book")
                .author("New Author")
                .price(BigDecimal.valueOf(25.00))
                .stockQuantity(5)
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDTO result = bookService.createBook(dto);

        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
    }
}
