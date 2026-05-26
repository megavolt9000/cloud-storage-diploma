package ru.netology.cloudstorage.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.repository.UserRepository;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter
        extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain

    ) throws ServletException, IOException {

        System.out.println(
                "REQUEST = " + request.getRequestURI()
        );

        String token =
                request.getHeader("auth-token");

        if (token == null || token.isBlank()) {

            token =
                    request.getHeader("Authorization");
        }

        if ((token == null || token.isBlank())
                && request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if ("auth-token".equals(
                        cookie.getName()
                )) {

                    token = cookie.getValue();

                    break;
                }
            }
        }

        if (token != null
                && token.startsWith("Bearer ")) {

            token = token.substring(7);
        }

        System.out.println(
                "TOKEN = " + token
        );

        if (token != null
                && !token.isBlank()) {

            UserEntity userEntity =
                    userRepository.findByToken(token)
                            .orElse(null);

            if (userEntity != null) {

                System.out.println(
                        "USER = "
                                + userEntity.getLogin()
                );

                User user =
                        new User(
                                userEntity.getLogin(),
                                userEntity.getPassword(),
                                Collections.emptyList()
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                authentication.setDetails(

                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println("AUTH OK");

            } else {

                System.out.println(
                        "USER NOT FOUND BY TOKEN"
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}