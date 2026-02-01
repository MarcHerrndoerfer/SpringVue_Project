package com.SpringProject.demo.auth;

import com.SpringProject.demo.auth.dto.LoginRequest;
import com.SpringProject.demo.auth.dto.MeResponse;
import com.SpringProject.demo.auth.dto.RegisterRequest;
import com.SpringProject.demo.user.User;
import com.SpringProject.demo.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/register")
  public MeResponse register(@RequestBody @Valid RegisterRequest req) {
    if (userRepository.findByEmail(req.email()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    User user = new User();
    user.setName(req.name());
    user.setEmail(req.email());
    user.setPasswordHash(passwordEncoder.encode(req.password()));
    user.setRole(req.role());
    user.setActive(true);

    user = userRepository.save(user);

    return new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }

  @PostMapping("/login")
  public MeResponse login(@RequestBody @Valid LoginRequest req, HttpServletRequest request) {
    User user = userRepository.findByEmail(req.email())
      .filter(User::isActive)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    var auth = new UsernamePasswordAuthenticationToken(
      user.getEmail(),
      null,
      List.of(new SimpleGrantedAuthority(user.getRole()))
    );

    SecurityContextHolder.getContext().setAuthentication(auth);
    request.getSession(true);

    return new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }

  @GetMapping("/me")
  public MeResponse me() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    User user = userRepository.findByEmail(authentication.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in"));

    return new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }

  @PostMapping("/logout")
  public void logout(HttpServletRequest request) {
    var session = request.getSession(false);
    if (session != null) session.invalidate();
    SecurityContextHolder.clearContext();
  }
}
