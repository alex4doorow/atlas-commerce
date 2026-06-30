package com.afa.atlas.commerce.order.controllers;

import com.afa.atlas.crypto.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.afa.atlas.commerce.order.controllers.internal.ControllerConstants.CRYPTO;

@RestController
@RequiredArgsConstructor
@RequestMapping(CRYPTO)
public class CryptoController {

    private final CryptoService cryptoService;

    @GetMapping("/test")
    public String test() {

        final String encrypted = cryptoService.encrypt("Hello Atlas");

        return cryptoService.decrypt(encrypted);
    }
}
