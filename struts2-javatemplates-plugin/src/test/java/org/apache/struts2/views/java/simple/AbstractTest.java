package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.easymock.EasyMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class AbstractTest extends TestCase {
    private Map<String, String> scriptingAttrs = new HashMap<String, String>();
    private Map<String, String> commonAttrs = new HashMap<String, String>();

    protected SimpleTheme theme;

    protected StringWriter writer;
    protected Map map;

    protected Template template;
    protected Map stackContext;
    protected ValueStack stack;
    protected TemplateRenderingContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected abstract UIBean getUIBean() throws Exception;

    protected abstract String getTagName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();    
        scriptingAttrs.put("onclick", "onclick_");
        scriptingAttrs.put("ondblclick", "ondblclick_");
        scriptingAttrs.put("onmousedown", "onmousedown_");
        scriptingAttrs.put("onmouseup", "onmouseup_");
        scriptingAttrs.put("onmouseover", "onmouseover_");
        scriptingAttrs.put("onmousemove", "onmousemove_");
        scriptingAttrs.put("onmouseout", "onmouseout_");
        scriptingAttrs.put("onfocus", "onfocus_");
        scriptingAttrs.put("onblur", "onblur_");
        scriptingAttrs.put("onkeypress", "onkeypress_");
        scriptingAttrs.put("onkeydown", "onkeydown_");
        scriptingAttrs.put("onkeyup", "onkeyup_");
        scriptingAttrs.put("onselect", "onselect_");
        scriptingAttrs.put("onchange", "onchange_");

        commonAttrs.put("accesskey", "accesskey_");

        theme = new SimpleTheme();
        writer = new StringWriter();
        map = new HashMap();

        template = org.easymock.classextension.EasyMock.createMock(Template.class);
        stack = EasyMock.createNiceMock(ValueStack.class);
        setUpStack();
        stackContext = new HashMap();

        context = new TemplateRenderingContext(template, writer, stack, map, null);
        stackContext.put(Component.COMPONENT_STACK, new Stack());

        request = EasyMock.createMock(HttpServletRequest.class);
        response = EasyMock.createMock(HttpServletResponse.class);

        EasyMock.expect(stack.getContext()).andReturn(stackContext).anyTimes();

        Container container = EasyMock.createNiceMock(Container.class);
        XWorkConverter converter = new ConverterEx();
        EasyMock.expect(container.getInstance(String.class, StrutsConstants.STRUTS_TAG_ALTSYNTAX)).andReturn("true").anyTimes();
        EasyMock.expect(container.getInstance(XWorkConverter.class)).andReturn(converter).anyTimes();
        stackContext.put(ActionContext.CONTAINER, container);


        EasyMock.replay(stack);
        EasyMock.replay(container);
    }

    protected static String s(String input) {
        return input.replaceAll("'", "\"");
    }

    protected void expectFind(String expr, Class toClass, Object returnVal) {
        EasyMock.expect(stack.findValue(expr, toClass)).andReturn(returnVal);
    }

    protected void expectFind(String expr, Object returnVal) {
        EasyMock.expect(stack.findValue(expr)).andReturn(returnVal);
    }

    protected void setUpStack() {
        //TODO setup a config with stack and all..for real
    }

    protected void applyScriptingAttrs(UIBean bean) {
        bean.setOnclick(scriptingAttrs.get("onclick"));
        bean.setOndblclick(scriptingAttrs.get("ondblclick"));
        bean.setOnmousedown(scriptingAttrs.get("onmousedown"));
        bean.setOnmouseup(scriptingAttrs.get("onmouseup"));
        bean.setOnmouseover(scriptingAttrs.get("onmouseover"));
        bean.setOnmousemove(scriptingAttrs.get("onmousemove"));
        bean.setOnmouseout(scriptingAttrs.get("onmouseout"));
        bean.setOnfocus(scriptingAttrs.get("onfocus"));
        bean.setOnblur(scriptingAttrs.get("onblur"));
        bean.setOnkeypress(scriptingAttrs.get("onkeypress"));
        bean.setOnkeydown(scriptingAttrs.get("onkeydown"));
        bean.setOnkeyup(scriptingAttrs.get("onkeyup"));
        bean.setOnselect(scriptingAttrs.get("onselect"));
        bean.setOnchange(scriptingAttrs.get("onchange"));
    }

    protected void applyCommonAttrs(UIBean bean) {
        bean.setAccesskey("accesskey_");
    }

    protected void assertScriptingAttrs(String str) {
        for (Map.Entry<String, String> entry : scriptingAttrs.entrySet()) {
            String substr = entry.getKey() + "=\"" + entry.getValue() + "\"";
            assertTrue("String [" + substr + "] was not found in [" + str + "]", str.indexOf(substr) >= 0);
        }
    }

    protected void assertCommongAttrs(String str) {
        for (Map.Entry<String, String> entry : commonAttrs.entrySet()) {
            String substr = entry.getKey() + "=\"" + entry.getValue() + "\"";
            assertTrue("String [" + substr + "] was not found in [" + str + "]", str.indexOf(substr) >= 0);
        }
    }

    protected Object doFindValue(String expr, Class toType) {
        Object val = stack.findValue(expr);

        if (toType == String.class)
            return val == null ? expr : val;
        else
            return val == null ? null : val;
    }

    public class ConverterEx extends XWorkConverter {
        public ConverterEx() {

        }
    }
}
