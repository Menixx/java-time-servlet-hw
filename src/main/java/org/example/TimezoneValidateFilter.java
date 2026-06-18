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

        // Валідація: TimeZone.getTimeZone повертає GMT для невідомих поясів
        TimeZone tz = TimeZone.getTimeZone(timezone);
        boolean isGMT = tz.getID().equals("GMT");
        boolean userSentGMT = timezone.equalsIgnoreCase("GMT")
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