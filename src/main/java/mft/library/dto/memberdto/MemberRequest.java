package mft.library.dto.memberdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberRequest(
        @NotBlank(message = "نام الزامی است")
        @Size(max = 100)
        String name,

        @NotBlank(message = "نام خانوادگی الزامی است")
        @Size(max = 100)
        String family,

        @NotBlank(message = "ایمیل الزامی است")
        @Email(message = "ایمیل معتبر نیست")
        String email,

        @NotBlank(message = "رمز عبور الزامی است")
        @Size(min = 6, max = 100, message = "رمز عبور باید حداقل ۶ کاراکتر باشد")
        String password
) {
}
