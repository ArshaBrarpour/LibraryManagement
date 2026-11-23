package mft.library.service;

import mft.library.dto.bookdto.BookDto;
import mft.library.dto.bookdto.BookRequest;
import mft.library.enums.LoanStatus;
import mft.library.enums.ReservationStatus;
import mft.library.exception.ApiException;
import mft.library.mapper.bookmapper.BookMapper;
import mft.library.model.Book;
import mft.library.repository.BookRepository;
import mft.library.repository.LoanRepository;
import mft.library.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private static final int MAX_PAGE_SIZE = 50;

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository,
                       ReservationRepository reservationRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.bookMapper = bookMapper;
    }

    // ------------------------------------------------------------------
    // ثبت کتاب (ادمین)
    // ------------------------------------------------------------------
    @Transactional
    public BookDto create(BookRequest request) {
        String isbn = request.isbn().trim();
        if (bookRepository.existsByIsbn(isbn)) {
            throw new ApiException(HttpStatus.CONFLICT, "کتابی با این شابک قبلاً ثبت شده است");
        }
        Book book = bookMapper.toEntity(request);
        book.setIsbn(isbn);
        book.setAvailableCopies(request.totalCopies());
        return bookMapper.toDto(bookRepository.save(book));
    }

    // ------------------------------------------------------------------
    // ویرایش کتاب (ادمین)
    // ------------------------------------------------------------------
    @Transactional
    public BookDto update(Long id, BookRequest request) {
        Book book = findBookForUpdate(id);
        String isbn = request.isbn().trim();
        if (bookRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "کتاب دیگری با این شابک وجود دارد");
        }
        // نسخه‌هایی که الان نزد کاربران است
        int borrowedCopies = (int) loanRepository.countByBookIdAndStatus(id, LoanStatus.ACTIVE);
        if (request.totalCopies() < borrowedCopies) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "تعداد کل نسخه‌ها نمی‌تواند کمتر از نسخه‌های در امانت (" + borrowedCopies + ") باشد");
        }
        bookMapper.updateEntity(request, book);
        book.setIsbn(isbn);
        // موجودی آزاد = کل نسخه‌ها - نسخه‌های در امانت
        book.setAvailableCopies(request.totalCopies() - borrowedCopies);
        return bookMapper.toDto(book);
    }

    // ------------------------------------------------------------------
    // مشاهده و جستجو (همه‌ی کاربران)
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        // قبلاً اگر کتاب نبود null برمی‌گشت؛ حالا خطای 404 درست برمی‌گردد
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کتاب پیدا نشد"));
    }

    // لیست صفحه‌بندی‌شده + جستجو (عنوان/نویسنده) + فیلتر دسته‌بندی + «فقط موجود»
    @Transactional(readOnly = true)
    public Page<BookDto> search(String search, String category, boolean onlyAvailable, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        String safeSearch = search == null ? "" : search.trim();
        String safeCategory = category == null ? "" : category.trim();

        Page<Book> books = bookRepository.search(safeSearch, safeCategory, onlyAvailable,
                PageRequest.of(safePage, safeSize, Sort.by("title").ascending().and(Sort.by("id"))));
        return books.map(bookMapper::toDto);
    }

    // ------------------------------------------------------------------
    // حذف کتاب (ادمین)
    // ------------------------------------------------------------------
    // تغییر: متد deleteById(id, request) که بدون هیچ بررسی کتاب را پاک می‌کرد حذف شد
    // (قانون «حذف فقط اگر امانت و صف رزرو نباشد» را رد می‌کرد). فقط همین متد می‌ماند.
    @Transactional
    public void delete(Long id) {
        Book book = findBookForUpdate(id);
        if (loanRepository.existsByBookIdAndStatus(id, LoanStatus.ACTIVE)) {
            throw new ApiException(HttpStatus.CONFLICT, "این کتاب الان در امانت است و حذف نمی‌شود");
        }
        if (reservationRepository.existsByBookIdAndStatus(id, ReservationStatus.WAITING)) {
            throw new ApiException(HttpStatus.CONFLICT, "برای این کتاب صف رزرو وجود دارد و حذف نمی‌شود");
        }
        // سابقه‌ی قدیمی امانت/رزرو (برگشته یا لغوشده) باید پاک شود وگرنه کلید خارجی اجازه‌ی حذف کتاب را نمی‌دهد
        loanRepository.deleteByBookId(id);
        reservationRepository.deleteByBookId(id);
        bookRepository.delete(book);
    }

    private Book findBookForUpdate(Long id) {
        return bookRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کتاب پیدا نشد"));
    }
}
