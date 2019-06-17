package org.oscm.identity.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.oscm.identity.error.IdentityProviderException;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
    public void loginPage(@RequestParam(value = "tenantId", required = false) String tenantId, HttpServletRequest request,
                          HttpServletResponse response) {


        TenantConfiguration configuration = tenantService.loadTenant(Optional.ofNullable(tenantId));
        String url = AuthorizationRequestManager.buildRequest(configuration.getProvider())
                .baseUrl(configuration.getAuthUrl())
                .clientId(configuration.getClientId())
                .redirectUrl(configuration.getIdTokenRedirectUrl())
                .scope("openid")
                .responseType("id_token")
                .responseMode("form_post")
                //TODO: create nonce which should be validated for id_token
                .nonce("test-nonce")
                .buildUrl();

        try {
            response.sendRedirect(url);
        } catch (IOException exc) {
            throw new IdentityProviderException("Problem with contacting identity provider", exc);
        }
    }

    @PostMapping("/token")
    public ModelAndView idTokenCallback(@RequestParam(value = "id_token", required = false) String idToken,
                                        @RequestParam(value = "error", required = false) String error,
                                        @RequestParam(value = "error_description", required = false) String errorDescription) {

        if (error != null) {
            throw new IdentityProviderException(error + ": " + errorDescription);
        }

        //TODO: validate the token and when it it successful send redirection back to oscm to put proper user into session context
        //TODO: cleanup this method - this is now only done for initial understanding id_token

        logger.info("Received id_token:" + idToken);
        DecodedJWT decodedToken = JWT.decode(idToken);

        ModelAndView view = new ModelAndView();
        view.addObject("idToken", idToken);
        view.addObject("expirationDate", decodedToken.getExpiresAt());
        view.addObject("name", decodedToken.getClaim("name").asString());
        view.addObject("uniqueName", decodedToken.getClaim("unique_name").asString());
        view.setViewName("idtoken");

        return view;
    }

}
