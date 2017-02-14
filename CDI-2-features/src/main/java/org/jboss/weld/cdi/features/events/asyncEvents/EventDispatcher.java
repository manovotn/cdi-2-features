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

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.inject.Inject;

import org.jboss.weld.cdi.features.events.EventPayload;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@ApplicationScoped
public class EventDispatcher {

    @Inject
    @Synchronous
    Event<Object> firstEvent;

    @Inject
    @Asynchronous
    Event<Object> secondaryEvent;

    public void fireSync() {
        firstEvent.fire(new EventPayload());
    }

    public CompletionStage<EventPayload> fireAsync(EventPayload payload) throws InterruptedException {
        return secondaryEvent.fireAsync(payload);
    }

    public CompletionStage<EventPayload> fireAsyncWithOptions(EventPayload payload) {
        NotificationOptions no = NotificationOptions.ofExecutor(new CustomExecutor());
        return secondaryEvent.fireAsync(payload, no);
    }
}
