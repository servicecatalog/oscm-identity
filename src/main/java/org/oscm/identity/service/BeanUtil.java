/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: Jul 26, 2019
 *
 *******************************************************************************/

package org.oscm.identity.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Simple service class for retrieving spring managed beans i.e. when referencing such a bean in the
 * class which is not managed by Spring context
 */
@Service
public class BeanUtil implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }

  /**
   * Returns Spring managed bean of the class type given.
   *
   * @param beanClass the class of the spring managed bean
   * @return
   */
  public static <T extends Object> T getBean(Class<T> beanClass) {
    return context.getBean(beanClass);
  }
}
