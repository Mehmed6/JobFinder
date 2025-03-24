package com.doganmehmet.app.utility;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext m_applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        m_applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass)
    {
        return m_applicationContext.getBean(beanClass);
    }
}
