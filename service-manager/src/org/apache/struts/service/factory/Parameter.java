/*
 * $Header: /home/cvs/jakarta-struts/contrib/service-manager/src/org/apache/struts/service/factory/Attic/Parameter.java,v 1.1 2001/07/14 22:41:57 oalexeev Exp $
 * $Revision: 1.1 $
 * $Date: 2001/07/14 22:41:57 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.struts.service.factory;

/** Container to store parameter info at run time. 
 *
 * @author Oleg V Alexeev
 * @version $Revision: 1.1 $ $Date: 2001/07/14 22:41:57 $
 */
public class Parameter {

        //----------------------------------------------------- protected variables

        /** Value of the parameter
         */
        protected Object value = null;

        /** Parent mapping with info about type of the parameter, its name, etc.
         */
        protected ParameterMapping mapping = null;

        //------------------------------------------------------ constructors

        /** Default costructor
         */
        public Parameter() {}

        /** Defines link to the parent mapping
         *  @param mapping - parent parameter mapping
         */
        public Parameter( ParameterMapping mapping ) {
                setMapping( mapping );
        }

        //------------------------------------------------------- properties support

        /** Mapping getter
         *  @return ParameterMapping - parent parameter mapping
         */
        public ParameterMapping getMapping() {
                return mapping;
        }

        /** Mapping setter
         *  @param mapping - parent parameter mapping
         */
        public void setMapping( ParameterMapping mapping ) {
                this.mapping = mapping;
        }

        /** Value getter
         *  @return Object - value of the parameter
         */
        public Object getValue() {
                return value;
        }

        /** Value setter
         *  @param value - new value of the parameter
         */
        public void setValue( Object value ) {
                this.value = value;
        }

}