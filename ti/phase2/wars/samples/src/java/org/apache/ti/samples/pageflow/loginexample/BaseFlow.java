/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.samples.pageflow.loginexample;

import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.NotLoggedInException;
import org.apache.ti.pageflow.annotations.ti;

@ti.controller(
    simpleActions={
        @ti.simpleAction(name="loginCancel", navigateTo=ti.NavigateTo.currentPage),
        @ti.simpleAction(name="loginSuccess", navigateTo=ti.NavigateTo.previousAction)
    },
    handleExceptions={
        // This sends control to the loginflow nested page flow upon any NotLoggedInException.
        @ti.handleException(type=NotLoggedInException.class, path="/pageflow/loginexample/loginflow/Controller.jpf")
    }
)
public abstract class BaseFlow
    extends PageFlowController
{
    @ti.forward(name="success", navigateTo=ti.NavigateTo.currentPage)
    public Forward logout()
    {
        logout(false);
        return new Forward("success");
    }
}
