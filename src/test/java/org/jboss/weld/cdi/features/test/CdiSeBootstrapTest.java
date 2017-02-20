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

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.jboss.weld.cdi.features.seBootstrap.BeanA;
import org.jboss.weld.cdi.features.seBootstrap.NotABean;
import org.jboss.weld.cdi.features.seBootstrap.subpackage.BeanB;
import org.jboss.weld.cdi.features.seBootstrap.subpackage.SomeInterceptor;
import org.jboss.weld.cdi.features.seBootstrap.subpackage.anotherPackage.BeanC;
import org.jboss.weld.cdi.features.seBootstrap.subpackage.anotherPackage.TotallyCoolExtension;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class CdiSeBootstrapTest {

    @Test
    public void testBasic() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        // SeContainer extends AutoCloseable, therefore we can use try-with-resources
        // you needn't do that though, just don't forget to call SeContainer#close()
        try (SeContainer container = initializer.initialize()) {
            Assert.assertTrue(container.isRunning());
        }

        // here is a snippet where we can handle it manually
        SeContainer manuallyHandledContainer = initializer.initialize();
        Assert.assertTrue(manuallyHandledContainer.isRunning());
        manuallyHandledContainer.close();

        // using the container after shutting it down obviously results in exception
        try {
            manuallyHandledContainer.getBeanManager();
        } catch (IllegalStateException e) {
            // OK, expected
            return;
        }
        Assert.fail();
    }

    @Test
    public void testAdvancedBootstrap() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();

        // Alright, we got the basics, how about some custom booting - synthetic archives? Sure!
        initializer.disableDiscovery();
        // now, pick what you would like to add into the archive, by package, or by bean class
        initializer.addPackages(true, BeanB.class.getPackage());
        initializer.addBeanClasses(BeanA.class);

        // since we disabled discovery, not even beans.xml will be picked up, so how do we declare interceptors etc?
        initializer.enableInterceptors(SomeInterceptor.class);

        // what about extensions? Sure, no problem.
        initializer.addExtensions(TotallyCoolExtension.class);

        try (SeContainer container = initializer.initialize()) {
            // verify we have the extension in place
            Assert.assertEquals(5, container.select(TotallyCoolExtension.class).get().getAllCaught().size());

            container.select(BeanC.class).get().ping();

            // verify that interceptor works as expected
            Assert.assertEquals(true, SomeInterceptor.invoked.get());

            // verify that NotABean wasn't picked up
            Assert.assertFalse(container.select(NotABean.class).isResolvable());
        }
    }
}
