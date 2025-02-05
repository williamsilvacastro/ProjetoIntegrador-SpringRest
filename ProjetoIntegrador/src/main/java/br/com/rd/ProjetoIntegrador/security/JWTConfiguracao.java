package br.com.rd.ProjetoIntegrador.security;

import br.com.rd.ProjetoIntegrador.service.DetalheClienteServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class JWTConfiguracao extends WebSecurityConfigurerAdapter {

    private final DetalheClienteServiceImpl clienteService;
    private final PasswordEncoder passwordEncoder;


    public JWTConfiguracao(DetalheClienteServiceImpl clienteService, PasswordEncoder passwordEncoder) {
        this.clienteService = clienteService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(clienteService).passwordEncoder(passwordEncoder);
    }



    @Override
    public void configure(WebSecurity web) throws Exception {
        // Ignora o filtro de recursos para os padrões de URL especificados
        web.ignoring().antMatchers("/resources/**", "/swagger-ui/**", "/v2/api-docs", "/webjars/**", "/swagger-resources/**");
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE, "/clienteEndereco/DeleteEndereco/{id_cliente}/{id_endereco}","/clienteCartao/delete/{id1}/{id2}").permitAll()
                .antMatchers(HttpMethod.POST, "/login", "/cadastroCliente/salvar", "/Card/multi", "/clienteCartao/create", "/Pedido/gerarNf"
                        ,"/formulariocontato", "/clienteEndereco/create").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui/", "/v2/api-docs", "/home/categorias", "/home/destaques", "/home/novidades", "/clienteCartao/cliente/{id}"
                        ,"/produtos", "/produtos/{id}","/Estoque/{id}", "/cadastro-cliente/senha/{email}", "/parcelas", "/cadastro-cliente/getByEmail/{email}",
                        "/produtos/por-categoria/{id}", "/produtos/por-marca/{id}", "/produtos/por-familia/{id}", "/Pedido/{id}", "/clienteEndereco/cliente/{id}"
                        , "/produtos/por-prato/{id}", "/Marca", "/Marca/{id}", "/Card/Marca/{id}", "/Card/{id_cat}/{id_marc}/{id_fam}/{id_prato}"
                        ,"/produtos/buscar/{id}","/formulariocontato/", "/preco/{id}", "/Card/todosDestaques", "/Card/multi","/Card/busca/{busca}", "/Card/{id}", "/preco/findAllById_produto/{id}").permitAll()
                .antMatchers(HttpMethod.PUT,"/cadastroCliente/alterarSenha", "/clienteEndereco/EndPrincipal/{id_cliente}/{id_endereco}", "/clienteEndereco/EndEntrega/{id_cliente}/{id_endereco}", "/Endereco/{id}","/clienteCartao/tornarPrincipal/{id1}/{id2}").permitAll()
                .anyRequest().authenticated()
                .and()
                .cors().configurationSource(corsConfigurationSource()).and()
                .addFilter(new JWTAutenticarFilter(authenticationManager()))
                .addFilter(new JWTValidarFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
