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
package org.jboss.weld.cdi.features.configurators;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class ObservingExtension implements Extension {

    /**
     * Remove all annotations present on this type; then add @Dependent
     */
    public void observePAT(@Observes ProcessAnnotatedType<TypeToBeChanged> pat) {
        // remove all annotations
        pat.configureAnnotatedType().removeAll()
            // add @Dependent on bean
            .add(Dependent.Literal.INSTANCE)
            // find `incrementCounter` method and add interceptor binding
            .filterMethods((m) -> m.getJavaMember().getName().equals("incrementCounter")).findFirst().get().add(Intercepted.Literal.INSTANCE);
    }

    /**
     * Add a brand new bean with transitive closure of Integer. 
     * The bean will be created with a producer and will have a value of 5.
     */
    public void registerNewBean(@Observes AfterBeanDiscovery event) {
        event.addBean().produceWith((i) -> 5).addTransitiveTypeClosure(Integer.class);
    }
}
