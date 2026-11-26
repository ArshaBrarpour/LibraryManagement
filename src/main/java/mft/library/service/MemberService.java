package mft.library.service;

import mft.library.dto.memberdto.MemberDto;
import mft.library.dto.memberdto.MemberRequest;
import mft.library.enums.Role;
import mft.library.exception.ApiException;
import mft.library.mapper.membermapper.MemberMapper;
import mft.library.model.Member;
import mft.library.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;

    public MemberService(MemberRepository memberRepository, MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MemberDto register(MemberRequest request) {
        String email = request.email().trim().toLowerCase();
        if (memberRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "این ایمیل قبلاً ثبت شده است");
        }
        Member member = memberMapper.toEntity(request);
        member.setName(request.name().trim());
        member.setFamily(request.family().trim());
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setRole(Role.USER);
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public MemberDto getMe(String email) {
        return memberMapper.toDto(getMemberByEmail(email));
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کاربر پیدا نشد"));
    }
}
