package mft.library.dto.memberdto;

import mft.library.enums.Role;

public record MemberDto(
        Long id,
        String name,
        String family,
        String email,
        Role role
) {
}
