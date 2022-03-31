package me.whiteship.demospringsecurityform.account;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "gunho",roles = "USER")  //<- 중복되는 코드 만든 어노테이션
public @interface WithUser {
}
