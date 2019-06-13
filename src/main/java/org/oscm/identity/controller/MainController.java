package org.oscm.identity.controller;

import org.oscm.identity.oidc.request.AuthorizationRequestManager;
import org.oscm.identity.oidc.tenant.TenantConfiguration;
import org.oscm.identity.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RestController
public class MainController {

    private Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public String homePage() {
        return "Welcome to the oscm-identity home page";
    }

    @GetMapping("/login")
    public void loginPage(@RequestParam(value = "tenantId", required = false) String tenantId, HttpServletResponse response) {

        TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));

        String url = AuthorizationRequestManager.buildRequest(configuration.getProvider())
                .baseUrl(configuration.getAuthUrl())
                .clientId(configuration.getClientId())
                .scope("openid")
                .responseType("id_token")
                .redirectUrl("http://localhost:9090/oscm-identity/token")
                .responseMode("form_post")
                //TODO: create nonce which should be validated for id_token
                .nonce("test-nonce")
                .buildUrl();

        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            //TODO: add exception handling
            e.printStackTrace();
        }
    }

    @PostMapping("/token")
    public String idTokenCallback(@RequestParam("id_token") String idToken) {

        //TODO: validate the token

        return "ID token: " + idToken;
    }

}
