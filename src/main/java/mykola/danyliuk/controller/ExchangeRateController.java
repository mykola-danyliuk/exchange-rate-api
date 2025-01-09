package mykola.danyliuk.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import mykola.danyliuk.configuration.GlobalConfiguration;
import mykola.danyliuk.service.ExchangeRateService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping(GlobalConfiguration.API_ENDPOINT)
@AllArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService service;

    @Operation(summary = "Get exchange rate from Currency A to Currency B",
        description = "Fetches the exchange rate from the specified base currency to the target currency.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rate"),
        @ApiResponse(responseCode = "400", description = "Invalid currency code supplied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Cacheable("exchangeRates")
    @GetMapping("/rate/{base}/{target}")
    public RateResponseDTO getRate(
        @Parameter(description = "Base currency code", example = "USD") @PathVariable String base,
        @Parameter(description = "Target currency code", example = "EUR") @PathVariable String target) {
        return new RateResponseDTO(service.getRate(base, target), service.getLastUpdated(), service.delayInSeconds());
    }

    @Schema(description = "Response DTO for exchange rate")
    public record RateResponseDTO(
        @Schema(description = "Exchange rate from base to target currency", example = "1.12") BigDecimal rate,
        @Schema(description = "Timestamp of the last update", example = "1627849200") Long lastUpdated,
        @Schema(description = "Delay in seconds since the last update", example = "60") Long delayInSeconds) {}

    @Operation(summary = "Get all exchange rates from Currency A",
        description = "Fetches all exchange rates from the specified base currency to all available target currencies.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all exchange rates"),
        @ApiResponse(responseCode = "400", description = "Invalid currency code supplied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Cacheable("allRates")
    @GetMapping("/rate/{base}")
    public RatesResponseDTO getAllRates(
        @Parameter(description = "Base currency code", example = "USD") @PathVariable String base) {
        return new RatesResponseDTO(service.getAllRates(base), service.getLastUpdated(), service.delayInSeconds());
    }

    @Schema(description = "Response DTO for all exchange rates")
    public record RatesResponseDTO(
        @Schema(description = "Map of target currencies and their exchange rates") Map<String, BigDecimal> rates,
        @Schema(description = "Timestamp of the last update", example = "1627849200") Long lastUpdated,
        @Schema(description = "Delay in seconds since the last update", example = "60") Long delayInSeconds) {}

    @Operation(summary = "Get value conversion from Currency A to Currency B",
        description = "Converts a specified value from the base currency to the target currency.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully converted value"),
        @ApiResponse(responseCode = "400", description = "Invalid currency code or value supplied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("convert/{base}/{target}")
    public ConvertedValueResponseDTO convertValue(
        @Parameter(description = "Base currency code", example = "USD") @PathVariable String base,
        @Parameter(description = "Target currency code", example = "EUR") @PathVariable String target,
        @Parameter(description = "Value to convert", example = "100.0") @RequestParam double value) {
        return new ConvertedValueResponseDTO(service.convertValue(base, target, value), service.getLastUpdated(), service.delayInSeconds());
    }

    @Schema(description = "Response DTO for converted value")
    public record ConvertedValueResponseDTO(
        @Schema(description = "Converted value in target currency", example = "112.0") BigDecimal convertedValue,
        @Schema(description = "Timestamp of the last update", example = "1627849200") Long lastUpdated,
        @Schema(description = "Delay in seconds since the last update", example = "60") Long delayInSeconds) {}

    @Operation(summary = "Get value conversion from Currency A to a list of supplied currencies",
        description = "Converts a specified value from the base currency to all available target currencies.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully converted value to all target currencies"),
        @ApiResponse(responseCode = "400", description = "Invalid currency code or value supplied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/convert/{base}")
    public ConvertedValuesResponseDTO convertValueToList(
        @Parameter(description = "Base currency code", example = "USD") @PathVariable String base,
        @Parameter(description = "Value to convert", example = "100.0") @RequestParam double value) {
        return new ConvertedValuesResponseDTO(service.convertValueToList(base, value), service.getLastUpdated(), service.delayInSeconds());
    }

    @Schema(description = "Response DTO for converted values to multiple currencies")
    public record ConvertedValuesResponseDTO(
        @Schema(description = "Map of target currencies and their converted values") Map<String, BigDecimal> convertedValues,
        @Schema(description = "Timestamp of the last update", example = "1627849200") Long lastUpdated,
        @Schema(description = "Delay in seconds since the last update", example = "60") Long delayInSeconds) {}
}