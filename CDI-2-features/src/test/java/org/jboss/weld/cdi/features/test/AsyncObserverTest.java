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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.jboss.weld.cdi.features.events.EventPayload;
import org.jboss.weld.cdi.features.events.asyncEvents.CustomExecutor;
import org.jboss.weld.cdi.features.events.asyncEvents.EventDispatcher;
import org.jboss.weld.cdi.features.events.asyncEvents.EventObserver;
import org.junit.Assert;
import org.junit.Test;

/**
 * A simple test showing asynchronous observers and their behaviour.
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class AsyncObserverTest {

    @Test
    public void testAsyncObserver() throws InterruptedException, ExecutionException {
        SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance();
        try (SeContainer seContainer = seContainerInitializer.initialize()) {

            EventDispatcher dispatcher = seContainer.select(EventDispatcher.class).get();
            EventObserver observer = seContainer.select(EventObserver.class).get();
            
            // Fire sync event, async observer will not be notified
            dispatcher.fireSync();
            Assert.assertEquals(1, observer.getTimesSyncObserver());
            Assert.assertEquals(0, observer.getTimesAsyncObserved());
            
            // Fire async event, only asyc observer will be notified
            CompletionStage<EventPayload> completionStage = dispatcher.fireAsync(new EventPayload());
            // since it's async, we give it some time to deliver result
            completionStage.toCompletableFuture().get();
            
            Assert.assertEquals(1, observer.getTimesSyncObserver());
            Assert.assertEquals(1, observer.getTimesAsyncObserved());
            
            // Fire async events with custom executor
            completionStage = dispatcher.fireAsyncWithOptions(new EventPayload());
            // again, let us wait so we can assert
            completionStage.toCompletableFuture().get();
            
            Assert.assertEquals(true, CustomExecutor.executed.get());
            Assert.assertEquals(1, observer.getTimesSyncObserver());
            Assert.assertEquals(2, observer.getTimesAsyncObserved());
        }
    }
}
