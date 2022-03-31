#Security

### spring Configuration
* 기본 설정 configure 오버라이드 하여 특정 url 허용해줄수 있다. 
```
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //anyRequest 는 matcher아닌 다른 url 기타 등등... authenticated 인증만 하면된다.
        //and 는 그리고 의미 -> and 안해도 된다.
        http.authorizeRequests()
                .mvcMatchers("/","/info","/account/**").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated();
        http.formLogin();
        http.httpBasic();
```

### UserDetailsService
* UserDetailsService 를 implements 하여 디비에서 유저 정보를 가져와서 인증 해줄수 있다.
* security에서 패스워드를 저장할댄 평문으로 저장을 못하고 PasswordEncoder를 통해 인코딩하여 저장해야한다.
```
@Service
public class AccountService implements UserDetailsService {

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
```

```
패스워드 인코더 빈등록 
@Bean
	public PasswordEncoder getPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
userDetails 를 상속받은 클래스에서 사용 

	@Autowired
    PasswordEncoder passwordEncoder;

이런식으로 서비스 단에서 패스워드를 인코딩하여 디비에 저장하는 로직을추가하여 사용할수있다.
public Account createNew(Account account) {
        account.encodePassword(passwordEncoder);
        return this.accountRepository.save(account);
    }
```

### 시큐리티 테스트
* 테스트시 @WithAnonymousUser @WithMockUser(username = "gunho",roles = "ADMIN") 통해 with메서드 대처하여 권한을 준 가정하여 테스트 할 수 있다
하지만 테스트시 중복된 코드를 많이 사용할 경우가 있어 어노테이션을 만들 수 있다. 
~~~
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "gunho",roles = "USER")  //<- 중복되는 코드 만든 어노테이션
public @interface WithUser {
}

테스트 하는 메서들에 @WithUser 사용하면 위에 USER 롤과 이름이 적용된다.

~~~
*@Transactional 트랜젝션하면 메서드 사용하고 롤백이 된다 다른 테스트에 영향을 주지않는다.