package mft.library.controller.bookcontroller;

import jakarta.validation.Valid;
import mft.library.dto.bookdto.BookDto;
import mft.library.dto.bookdto.BookRequest;
import mft.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * مدیریت و جستجوی کتاب‌ها
 *  GET    /api/books?search=&category=&onlyAvailable=&page=&size=  -> لیست صفحه‌بندی‌شده (همه‌ی کاربران)
 *  GET    /api/books/{id}                                          -> یک کتاب (همه‌ی کاربران)
 *  POST   /api/books                                               -> ثبت کتاب (فقط ADMIN)
 *  PUT    /api/books/{id}                                          -> ویرایش کتاب (فقط ADMIN)
 *  DELETE /api/books/{id}                                          -> حذف کتاب (فقط ADMIN)
 */
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Page<BookDto> search(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "category", defaultValue = "") String category,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return bookService.search(search, category, onlyAvailable, page, size);
    }

    @GetMapping("/{id}")
    public BookDto getById(@PathVariable("id") Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable("id") Long id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        bookService.delete(id);
    }
}
