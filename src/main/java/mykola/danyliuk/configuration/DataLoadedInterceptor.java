package mykola.danyliuk.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import mykola.danyliuk.service.CurrenciesService;
import mykola.danyliuk.service.ExchangeRateService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class DataLoadedInterceptor implements HandlerInterceptor {

    private final ExchangeRateService exchangeRateService;
    private final CurrenciesService currenciesService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!exchangeRateService.isLoaded() && !currenciesService.isLoaded() && request.getRequestURI().contains(GlobalConfiguration.API_ENDPOINT)) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("Service is currently unavailable. Please try again later.");
            return false;
        }
        return true;
    }
}