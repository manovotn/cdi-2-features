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
package org.jboss.weld.cdi.features.contextActivation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.context.control.RequestContextController;
import javax.inject.Inject;

/**
 * A bean injecting another @RequestScoped bean and using it.
 * 
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@ApplicationScoped
public class Foo {
    
    @Inject
    RequestScopedBean bean; // bean itself
    
    @Inject
    RequestContextController controller; // built-in controller allowing to activate the scope
    
    
    // Activate request context, therefore making it possible to operate with the bean without any actual request
    @ActivateRequestContext
    public String activateViaInterceptor() {
        return bean.sayHello();
    }
    
    public String activateViaController() {
        // activate context
        controller.activate();
        String result = bean.sayHello();
        // note that now we are responsible for deactivation as well
        controller.deactivate();
        return result;
    }
}
