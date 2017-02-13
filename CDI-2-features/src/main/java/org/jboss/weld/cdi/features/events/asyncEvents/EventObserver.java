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
package org.jboss.weld.cdi.features.events.asyncEvents;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;

import org.jboss.weld.cdi.features.events.EventPayload;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@ApplicationScoped
public class EventObserver {

    private final AtomicInteger timesAsyncObserved = new AtomicInteger(0);
    private final AtomicInteger timesSyncObserved = new AtomicInteger(0);

    public int getTimesAsyncObserved() {
        return timesAsyncObserved.get();
    }

    public int getTimesSyncObserver() {
        return timesSyncObserved.get();
    }

    public void observeSync(@Observes @Synchronous EventPayload payload) {
        timesSyncObserved.incrementAndGet();
    }

    public void observeAsync(@ObservesAsync @Asynchronous EventPayload payload) {
        timesAsyncObserved.incrementAndGet();
    }
}
