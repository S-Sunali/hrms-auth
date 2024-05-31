package com.adt.authservice.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.adt.authservice.util.TableDataExtractor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

@Configuration
public class Auth {
	String AUTHORITIES_CLAIM = "authorities";

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private TableDataExtractor dataExtractor;

	public boolean allow(String apiName, Map<String, String> resourceAttributes) {
		String token = getToken(request);
		Claims claims = getUserIdFromJWT(token);
		List<GrantedAuthority> authorities = getAuthorities(claims);	
		boolean isValidAuthority = false;
		String authority=null;
		String sql = "SELECT r.role_name FROM user_schema.role r JOIN av_schema.api_mapping  am ON r.role_id = am.role_id JOIN av_schema.api_details ad ON am.api_id = ad.api_id WHERE ad.api_name ="
				+ "'" + apiName + "'";
		List<Map<String, Object>> roleData = dataExtractor.extractDataFromTable(sql);
		for (Map<String, Object> role : roleData) {
			authority = String.valueOf(role.get("role_name"));
			isValidAuthority = checkAuthority(authority, authorities);
			if (isValidAuthority) {
				break;
			}
		}
		boolean isValidResourse = checkResourse(resourceAttributes, claims);
		return isValidResourse && isValidAuthority;
	}

	public boolean allow(String apiName) {
		String token = getToken(request);
		Claims claims = getUserIdFromJWT(token);
		List<GrantedAuthority> authorities = getAuthorities(claims);
		boolean isValidAuthority = false;
		String authority=null;
		String sql = "SELECT r.role_name FROM user_schema.role r JOIN av_schema.api_mapping  am ON r.role_id = am.role_id JOIN av_schema.api_details ad ON am.api_id = ad.api_id WHERE ad.api_name ="+"'"+apiName+"'";
		List<Map<String, Object>> roleData = dataExtractor.extractDataFromTable(sql);
		for (Map<String, Object> role : roleData) {
	    	 authority = String.valueOf(role.get("role_name"));
	    	 isValidAuthority = checkAuthority(authority, authorities);
	    	 if (isValidAuthority) {
					break;
				}
		}
		return isValidAuthority;
	}

	private boolean checkResourse(Map<String, String> resourceAttributes, Claims claims) {
		boolean isValidResourse = true;
		String employeeId = String.valueOf(claims.get("id"));
		if (resourceAttributes != null) {
			if (resourceAttributes.containsKey("currentUser")) {
				String currentUserId = resourceAttributes.get("currentUser");
				currentUserId = currentUserId != null ? currentUserId.trim() : "";
				isValidResourse = (currentUserId.equalsIgnoreCase(employeeId));
			}
		}
		return isValidResourse;
	}

	private boolean checkAuthority(String authority, List<GrantedAuthority> authorities) {
		return authorities.stream().anyMatch(s -> {
			s = (SimpleGrantedAuthority) s;
			return s.getAuthority().equalsIgnoreCase(authority);
		});
	}

	private List<GrantedAuthority> getAuthorities(Claims claims) {
		List<GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_CLAIM).toString().split(","))
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		return authorities;
	}

	private String getToken(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		token = token != null ? token.trim() : "";
		token = token.replace("Bearer", "");
		token = token != null ? token.trim() : "";
		return token;
	}

	@SuppressWarnings("rawtypes")
	public Claims getUserIdFromJWT(String token) {
		int i = token.lastIndexOf('.');
		String withoutSignature = token.substring(0, i + 1);
		Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
		return untrusted.getBody();
	}

}
