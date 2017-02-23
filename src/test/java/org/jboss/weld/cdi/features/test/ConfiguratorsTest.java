/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

import org.jboss.weld.cdi.features.configurators.DumbInterceptor;
import org.jboss.weld.cdi.features.configurators.ObservingExtension;
import org.jboss.weld.cdi.features.configurators.TypeToBeChanged;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class ConfiguratorsTest {

    @Test
    public void testPATConfigurator() {
        // we want to test that TypeToBeChanged is now @Dependent and with not qualifier
        SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance();
        seContainerInitializer.disableDiscovery().addPackages(ObservingExtension.class.getPackage()).addExtensions(ObservingExtension.class);
        try (SeContainer seContainer = seContainerInitializer.initialize()) {
            // retrieve two instances of this type - as @Dependent, they should differ
            // since we removed the qualifier, we can also omit it here
            TypeToBeChanged beanOne = seContainer.select(TypeToBeChanged.class).get();
            TypeToBeChanged beanTwo = seContainer.select(TypeToBeChanged.class).get();

            // increment counter in one bean and assert that only one changed
            beanOne.incrementCounter();
            Assert.assertEquals(0, beanTwo.getCounter());
            Assert.assertEquals(1, beanOne.getCounter());

            // we also added interceptor binding; let's assert it was invoked once
            Assert.assertEquals(1, DumbInterceptor.timesInvoked.get());
        }
    }

    @Test
    public void testBeanAddition() {
        SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance();
        seContainerInitializer.disableDiscovery().addPackages(ObservingExtension.class.getPackage()).addExtensions(ObservingExtension.class);
        try (SeContainer seContainer = seContainerInitializer.initialize()) {
            // assert that we can select a Number bean with value 5
            // note that we are selecting type Number because it was added with transitive closure
            Assert.assertEquals(new Integer(5), seContainer.select(Number.class).get());
        }
    }
}
