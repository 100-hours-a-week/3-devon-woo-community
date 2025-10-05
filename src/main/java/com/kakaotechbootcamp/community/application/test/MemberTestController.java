package com.kakaotechbootcamp.community.application.test;

import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test/members")
@RequiredArgsConstructor
public class MemberTestController {

    private final MemberRepository memberRepository;

    @PostMapping("/init")
    public String initTestData() {
        Member member1 = Member.builder()
                .email("test1@example.com")
                .password("password1")
                .nickname("테스트유저1")
                .profileImageUrl("https://example.com/profile1.jpg")
                .build();

        Member member2 = Member.builder()
                .email("test2@example.com")
                .password("password2")
                .nickname("테스트유저2")
                .profileImageUrl("https://example.com/profile2.jpg")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        return "Test data initialized";
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Member> getMember(@PathVariable Long id) {
        return memberRepository.findById(id);
    }

    @GetMapping("/count")
    public long getCount() {
        return memberRepository.count();
    }

    @PostMapping
    public Member createMember(@RequestBody CreateMemberRequest request) {
        Member member = Member.builder()
                .email(request.email)
                .password(request.password)
                .nickname(request.nickname)
                .profileImageUrl(request.profileImageUrl)
                .build();
        return memberRepository.save(member);
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody CreateMemberRequest request) {
        Member member = Member.builder()
                .id(id)
                .email(request.email)
                .password(request.password)
                .nickname(request.nickname)
                .profileImageUrl(request.profileImageUrl)
                .build();
        return memberRepository.save(member);
    }

    public record CreateMemberRequest(
            String email,
            String password,
            String nickname,
            String profileImageUrl
    ) {}
}