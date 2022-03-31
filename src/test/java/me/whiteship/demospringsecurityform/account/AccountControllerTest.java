package me.whiteship.demospringsecurityform.account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
//Mock 가정 하는것
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    //anonynous() 비인증 사용자를 의미
   @Test
   @WithAnonymousUser //어노테이션으로 with 대신함
   public void index_anonymous() throws Exception {
       //mockMvc.perform(get("/").with(anonymous()))
       mockMvc.perform(get("/"))
               .andDo(print())
               .andExpect(status().isOk());
   }

   //with(user("gunho").roles("USER"))) <- 이런게 잇다고 가정하는것 db에서 가져오는거 x
    @Test
    @WithUser // <- 새로 만든 어노테이션
    public void index_user() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUser
    public void admin_user() throws Exception {
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "gunho",roles = "ADMIN")
    public void admin_admin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional //트랜젝션하면 메서드 사용하고 롤백이 된다 다른 테스트에 영향을 주지않는다.
    public void login_success() throws Exception {
        Account account = this.accountUser("gunho","123");
        mockMvc.perform(formLogin().user(account.getUsername()).password("123"))
                .andExpect(authenticated());
    }

    @Test
    @Transactional
    public void login_fail() throws Exception {
        Account account = this.accountUser("gunho","123123123");
        mockMvc.perform(formLogin().user(account.getUsername()).password("123"))
                .andExpect(unauthenticated());
    }

    private Account accountUser(String username,String passwor) {
       Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwor);
        account.setRole("USER");
        return accountService.createNew(account);
    }

}