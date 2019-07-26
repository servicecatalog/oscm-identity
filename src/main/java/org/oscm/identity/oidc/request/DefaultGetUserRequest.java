/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/
package org.oscm.identity.oidc.request;

import lombok.Getter;
import lombok.Setter;
import org.oscm.identity.oidc.request.proxy.ProxyHandler;
import org.oscm.identity.service.BeanUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class DefaultGetUserRequest extends UserRequest {

  @Getter @Setter private String select;

  ProxyHandler proxyHandler = BeanUtil.getBean(ProxyHandler.class);

  @Override
  public ResponseEntity execute() {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(getToken());

    HttpEntity entity = new HttpEntity(headers);

    if (proxyHandler.isRequired()) {
      SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
      Proxy proxy =
          new Proxy(
              Proxy.Type.HTTP,
              new InetSocketAddress(proxyHandler.getHost(), proxyHandler.getPort()));
      factory.setProxy(proxy);
      restTemplate.setRequestFactory(factory);
    }

    String url = getBaseUrl() + "/" + getUserId() + "?$select=" + getSelect();

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    return responseEntity;
  }
}
