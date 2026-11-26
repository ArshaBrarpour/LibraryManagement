package mft.library.controller.membercontroller;

import jakarta.validation.Valid;
import mft.library.dto.memberdto.MemberDto;
import mft.library.dto.memberdto.MemberRequest;
import mft.library.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/members")
public class MemberRestController {

    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberDto register(@Valid @RequestBody MemberRequest request) {
        return memberService.register(request);
    }

    @PostMapping("/login")
    public MemberDto login(Principal principal) {
        return memberService.getMe(principal.getName());
    }

    @GetMapping("/me")
    public MemberDto me(Principal principal) {
        return memberService.getMe(principal.getName());
    }
}
