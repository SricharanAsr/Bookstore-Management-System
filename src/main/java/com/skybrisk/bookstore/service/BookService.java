package com.skybrisk.bookstore.service;

import com.skybrisk.bookstore.dto.BookDTO;
import com.skybrisk.bookstore.entity.Book;
import com.skybrisk.bookstore.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<BookDTO> getAllBooks(String query, Pageable pageable) {
        Page<Book> books;
        if (query != null && !query.trim().isEmpty()) {
            books = bookRepository.searchBooks(query, pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }
        return books.map(this::mapToDTO);
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return mapToDTO(book);
    }

    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        Book book = mapToEntity(bookDTO);
        return mapToDTO(bookRepository.save(book));
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setGenre(bookDTO.getGenre());
        book.setIsbn(bookDTO.getIsbn());
        book.setPrice(bookDTO.getPrice());
        book.setDescription(bookDTO.getDescription());
        book.setStockQuantity(bookDTO.getStockQuantity());
        book.setImageUrl(bookDTO.getImageUrl());

        return mapToDTO(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    private BookDTO mapToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .description(book.getDescription())
                .stockQuantity(book.getStockQuantity())
                .imageUrl(book.getImageUrl())
                .build();
    }

    private Book mapToEntity(BookDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .genre(dto.getGenre())
                .isbn(dto.getIsbn())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .stockQuantity(dto.getStockQuantity())
                .imageUrl(dto.getImageUrl())
                .build();
    }
}
