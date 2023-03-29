package com.toongather.toongather.global.security.service;

import com.toongather.toongather.domain.member.domain.Member;
import com.toongather.toongather.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String userKey) throws UsernameNotFoundException {

        //userKey -> jwt 에서 넣은 user pk id
        Member member = memberRepository.findOne(Long.valueOf(userKey));

        return Optional.ofNullable(member)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    }

}
