package me.whiteship.demospringsecurityform.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    // TODO {noop}123 <- security 패스워드 등록시 {} 알고리즘 안에 넣어 등록해야 등록됨
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    //userDetailService imple <- 스프링 시큐리티에서 사용 디비를 통해 유저 정보를 가져와 인증할떄 사용하는 인터페이스
    //db에서 유저정보를 가져와서 UserDetails return 해야함
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if(account == null) {
            throw new UsernameNotFoundException(username);
        }

        //User클래스에 빌더를 사용하면 엔티티객체를 UserDetails 객체로 바꿀수있다.
        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account createNew(Account account) {
        account.encodePassword(passwordEncoder);
        return this.accountRepository.save(account);
    }
}
