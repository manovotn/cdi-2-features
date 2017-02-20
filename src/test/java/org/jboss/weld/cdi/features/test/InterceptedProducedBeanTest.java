/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.cdi.features.test;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.jboss.weld.cdi.features.interceptedProducedBeans.AfterBeanDiscoveryObserver;
import org.jboss.weld.cdi.features.interceptedProducedBeans.CounterInterceptor;
import org.jboss.weld.cdi.features.interceptedProducedBeans.ProducedBean;
import org.jboss.weld.cdi.features.interceptedProducedBeans.TotallyDifferent;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class InterceptedProducedBeanTest {

    @Test
    public void interceptProducedBean() {
        resetCounter();
        
        SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance();
        try (SeContainer seContainer = seContainerInitializer.initialize()) {
            // get the ProducedBean CDI managed instance and invoke intercepted method
            ProducedBean producedBean = seContainer.select(ProducedBean.class, Default.Literal.INSTANCE).get();
            producedBean.ping();

            // assert that the method was intercepted
            Assert.assertEquals(1, CounterInterceptor.timesInvoked.get());
        }
    }

    @Test
    public void interceptProducedBeanViaExtension() {
        resetCounter();
        
        SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance();
        seContainerInitializer.disableDiscovery().addPackages(CounterInterceptor.class.getPackage()).addExtensions(AfterBeanDiscoveryObserver.class);
        try (SeContainer seContainer = seContainerInitializer.initialize()) {
            // get the ProducedBean CDI managed instance and invoke intercepted method
            ProducedBean producedBean = seContainer.select(ProducedBean.class, TotallyDifferent.Literal.INSTANCE).get();
            producedBean.ping();

            // assert that the method was intercepted
            Assert.assertEquals(1, CounterInterceptor.timesInvoked.get());
        }
    }
    
    
   private void resetCounter() {
        CounterInterceptor.timesInvoked.set(0);
        Assert.assertEquals(0, CounterInterceptor.timesInvoked.get());
   }
}
