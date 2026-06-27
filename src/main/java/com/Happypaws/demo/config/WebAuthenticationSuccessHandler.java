package com.Happypaws.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class WebAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	public WebAuthenticationSuccessHandler() {
		setDefaultTargetUrl("/dashboard");
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		Set<String> authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		String targetUrl = authorities.contains("ROLE_CLIENTE") ? "/dashboard/cliente" : "/dashboard/colaboradores";

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}