package com.agendamentos.adapter.inbound;

import com.agendamentos.adapter.inbound.dto.DefinirSenhaRequest;
import com.agendamentos.adapter.inbound.dto.LoginRequest;
import com.agendamentos.adapter.inbound.dto.LoginResponse;
import com.agendamentos.adapter.outbound.auth.JwtService;
import com.agendamentos.adapter.outbound.persistence.jpa.PrestadorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final PrestadorJpaRepository prestadorJpaRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var entity = prestadorJpaRepository.findById(request.prestadorId()).orElse(null);
        if (entity == null || entity.getSenha() == null) {
            return ResponseEntity.status(401).build();
        }
        if (!passwordEncoder.matches(request.senha(), entity.getSenha())) {
            return ResponseEntity.status(401).build();
        }
        var token = jwtService.gerarToken(entity.getId());
        return ResponseEntity.ok(new LoginResponse(token, entity.getId(), entity.getNomeNegocio()));
    }

    @PostMapping("/prestadores/{id}/senha")
    public ResponseEntity<Void> definirSenha(@PathVariable UUID id,
                                             @RequestBody DefinirSenhaRequest request) {
        var entity = prestadorJpaRepository.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        entity.setSenha(passwordEncoder.encode(request.senha()));
        prestadorJpaRepository.save(entity);
        return ResponseEntity.ok().build();
    }

}
