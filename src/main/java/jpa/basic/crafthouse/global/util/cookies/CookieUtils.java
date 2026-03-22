package jpa.basic.crafthouse.global.util.cookies;

import jakarta.servlet.http.Cookie;
import jpa.basic.crafthouse.global.util.security.AuthConstants;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    public Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(AuthConstants.COOKIE_PATH_ROOT);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        return cookie;
    }

    public Cookie deleteCookie(String name) {
        return createCookie(name, null, 0);
    }
}