<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="saf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><saf:text name="index.title"/></title>
    <link href="<saf:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body>
<h3><saf:text name="index.heading"/></h3>

<ul>
    <li><a href="<saf:url action="Registration!input"/>"><saf:text
            name="index.registration"/></a></li>
    <li><a href="<saf:url action="Logon!input"/>"><saf:text
            name="index.logon"/></a></li>
</ul>

<h3>Language Options</h3>
<ul>
    <li><a href="<saf:url action="Welcome?request_locale=en"/>">English</a></li>
    <li><a href="<saf:url action="Welcome?request_locale=ja"/>">Japanese</a></li>
    <li><a href="<saf:url action="Welcome?request_locale=ru"/>">Russian</a></li>
</ul>

<hr/>

<p><saf:i18n name="alternate">
    <img src="<saf:text name="struts.logo.path"/>"
         alt="<saf:text name="struts.logo.alt"/>"/>
</saf:i18n></p>

<p><a href="<saf:url action="Tour" />"><saf:text name="index.tour"/></a></p>

</body>
</html>
