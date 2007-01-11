<%--
/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
--%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<%-- Layout component 
  Render a list on severals columns
  parameters : numCols, list0, list1, list2, list3, ... 
--%>

<tiles:useAttribute id="numColsStr" name="numCols" classname="java.lang.String" />


<table>
<tr>
<%
int numCols = Integer.parseInt(numColsStr);
for( int i=0; i<numCols; i++ )
  {
%>
<tiles:importAttribute toName="list" name="<%="list" + i %>"/>
<td valign="top">
  <tiles:insertTemplate template="/layout/vboxLayout.jsp" flush="true" >
    <tiles:put name="componentsList" value="${pageScope.list}" />
  </tiles:insertTemplate>
</td>
<%
  } // end loop
%>
</tr>
</table>






