package mykola.danyliuk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import mykola.danyliuk.configuration.GlobalConfiguration;
import mykola.danyliuk.service.CurrenciesService;
import mykola.danyliuk.service.Currency;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(GlobalConfiguration.API_ENDPOINT)
@AllArgsConstructor
public class CurrenciesController {

    private final CurrenciesService service;

    @Operation(summary = "Get list of available currencies",
        description = "Fetches the list of all available currencies.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of currencies"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/currencies")
    public Collection<Currency> getCurrencies() {
        return service.getCurrencies();
    }
}