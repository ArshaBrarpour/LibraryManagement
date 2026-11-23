package mft.library.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // خطاهای خودمان (کتاب پیدا نشد، موجودی تمام شد، ...)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    // خطای اعتبارسنجی ورودی (@Valid): مثلاً ایمیل اشتباه یا عنوان خالی
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "اطلاعات ارسال‌شده معتبر نیست");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    // اضافه شد: JSON خراب یا خالی فرستاده شده
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "بدنه‌ی درخواست خالی یا نامعتبر است");
        return ResponseEntity.badRequest().body(body);
    }

    // اضافه شد: مثلاً دو درخواست همزمان با یک ایمیل/شابک یکسان؛ قید unique دیتابیس خطا می‌دهد.
    // بدون این هندلر کاربر خطای 500 می‌گرفت.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "این اطلاعات قبلاً ثبت شده است");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
