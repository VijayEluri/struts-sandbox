/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.ti.pageflow.internal;

import org.apache.ti.pageflow.PageFlowException;

/**
 * ServletException derivative thrown when an exception occurred during action processing, and the exception was not
 * handled by any exception handler.  This wrapper is used to prevent any outer try/catch blocks from re-processing
 * the exception.
 */
public class UnhandledException
        extends PageFlowException {

    public UnhandledException(Throwable baseCause) {
        super(baseCause);
    }
}
