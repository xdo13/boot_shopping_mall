package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Setter
@Getter
@ToString
@Entity
@Builder
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy =  GenerationType.IDENTITY)

    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = Member.builder()
                .role(Role.USER)
                .email(memberFormDto.getEmail())
                .address(memberFormDto.getAddress())
                .name(memberFormDto.getName())
                .password(passwordEncoder.encode(memberFormDto.getPassword()))
                .build();

        String password = memberFormDto.getPassword();
        return member;
    }
}
