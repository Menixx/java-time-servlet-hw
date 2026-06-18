package org.example;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String timezone = req.getParameter("timezone");

        // Якщо параметр не переданий — пропускаємо далі
        if (timezone == null || timezone.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        // Замінюємо пробіл на "+" (браузер кодує UTC+2 як "UTC 2")
        timezone = timezone.replace(" ", "+");

        // Java розуміє GMT+2 але не UTC+2
        String tzForValidation = timezone.replace("UTC+", "GMT+")
                .replace("UTC-", "GMT-");

        // Валідація
        TimeZone tz = TimeZone.getTimeZone(tzForValidation);
        boolean isGMT = tz.getID().equals("GMT");
        boolean userSentGMT = tzForValidation.equalsIgnoreCase("GMT")
                || timezone.equalsIgnoreCase("UTC");

        if (isGMT && !userSentGMT) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().write("<h2>Invalid timezone</h2>");
            return;
        }

        // Timezone валідний — пропускаємо запит далі до сервлету
        chain.doFilter(request, response);
    }
}