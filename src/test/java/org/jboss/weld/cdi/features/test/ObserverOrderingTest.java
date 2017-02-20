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
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.cdi.features.events.EventPayload;
import org.jboss.weld.cdi.features.events.observerOrdering.ObservingBean;
import org.junit.Assert;
import org.junit.Test;

/**
 * A simple test showing observer ordering in action.
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class ObserverOrderingTest {

    @Test
    public void testObserverOrdering() {
        SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance();
        try (SeContainer seContainer = seContainerInitializer.initialize()) {
            
            // get BeanManager and fire event for observer
            BeanManager bm = seContainer.getBeanManager();
            bm.fireEvent(new EventPayload());
            
            // assert that observer contains the correct sentence
            ObservingBean observer = seContainer.select(ObservingBean.class).get();
            Assert.assertEquals("Hello world!", observer.getSentence());
        }
    }
}
