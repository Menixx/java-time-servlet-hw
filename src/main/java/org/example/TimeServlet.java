package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init() {
        JavaxServletWebApplication application =
                JavaxServletWebApplication.buildApplication(getServletContext());

        WebApplicationTemplateResolver resolver =
                new WebApplicationTemplateResolver(application);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String timezone = req.getParameter("timezone");

        if (timezone != null) {
            timezone = timezone.replace(" ", "+");
        }

        // Якщо timezone не переданий — шукаємо в Cookie
        if (timezone == null || timezone.isBlank()) {
            timezone = getTimezoneFromCookie(req);
        }

        // Якщо і в Cookie немає — UTC за замовчуванням
        if (timezone == null || timezone.isBlank()) {
            timezone = "UTC";
        }

        // Зберігаємо timezone в Cookie
        Cookie cookie = new Cookie("lastTimezone", timezone.replace("+", "%2B"));
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 днів
        resp.addCookie(cookie);

        // Обчислюємо час
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        String formattedTime = now.format(FORMATTER) + " " + timezone;

        // Передаємо у Thymeleaf
        resp.setContentType("text/html; charset=UTF-8");

        JavaxServletWebApplication application =
                JavaxServletWebApplication.buildApplication(getServletContext());

        WebContext context = new WebContext(
                application.buildExchange(req, resp),
                req.getLocale()
        );
        context.setVariable("time", formattedTime);

        templateEngine.process("time", context, resp.getWriter());
    }

    private String getTimezoneFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastTimezone".equals(c.getName())) {
                    return c.getValue().replace("%2B", "+");
                }
            }
        }
        return null;
    }
}