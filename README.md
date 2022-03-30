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