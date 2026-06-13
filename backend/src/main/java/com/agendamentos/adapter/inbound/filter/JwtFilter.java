package com.agendamentos.adapter.inbound.filter;

import com.agendamentos.adapter.outbound.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        var path = request.getRequestURI();

        if (isPublico(path)) {
            chain.doFilter(request, response);
            return;
        }

        var header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            var token = header.substring(7);
            if (jwtService.isValido(token)) {
                request.setAttribute("prestadorId", jwtService.extrairPrestadorId(token));
            }
        }

        if (request.getAttribute("prestadorId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\":\"Não autenticado\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublico(String path) {
        return path.startsWith("/api/auth/")
                || path.startsWith("/webhook/")
                || path.equals("/api/prestadores")
                || (path.startsWith("/api/prestadores/") && path.endsWith("/link"))
                || path.equals("/actuator/health");
    }

}
