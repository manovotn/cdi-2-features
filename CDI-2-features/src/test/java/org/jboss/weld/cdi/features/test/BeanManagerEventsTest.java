package org.jboss.weld.cdi.features.test;

import java.util.concurrent.ExecutionException;

import javax.enterprise.event.Event;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.cdi.features.events.EventPayload;
import org.jboss.weld.cdi.features.events.asyncEvents.Asynchronous;
import org.jboss.weld.cdi.features.events.asyncEvents.EventObserver;
import org.jboss.weld.cdi.features.events.asyncEvents.Synchronous;
import org.junit.Assert;
import org.junit.Test;

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

/**
 * A test showing how to fire all kinds of event from a BeanManager
 * 
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class BeanManagerEventsTest {
    
    @Test
    public void fireEventFromManagerTest() throws InterruptedException, ExecutionException {
        SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance();
        try (SeContainer seContainer = seContainerInitializer.initialize()) {
            // Observer for the sake of assertions
            EventObserver observer = seContainer.select(EventObserver.class).get();
            
            // get BeanManager, this is equal to injecting it into a bean
            BeanManager bm = seContainer.getBeanManager();
            
            // Use BM to get hands on Event interface
            Event<Object> event = bm.getEvent();
            
            // Fire all kind of events
            event.fire(new EventPayload());
            event.select(EventPayload.class, Synchronous.Literal.INSTANCE).fire(new EventPayload());
            Assert.assertEquals(1, observer.getTimesSyncObserver());
                
            // Everyone likes short code, so here is a one-liner
            bm.getEvent().select(Asynchronous.Literal.INSTANCE).fireAsync(new EventPayload()).toCompletableFuture().get();
            Assert.assertEquals(1, observer.getTimesAsyncObserved());
        }
    }
}
