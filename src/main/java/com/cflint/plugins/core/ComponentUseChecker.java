package com.cflint.plugins.core;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.*;
import com.cflint.BugList;
import com.cflint.CF;
import com.cflint.plugins.CFLintScannerAdapter;
import com.cflint.plugins.Context;

import net.htmlparser.jericho.Element;
import ro.fortsoft.pf4j.Extension;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;

import com.cflint.tools.CFTool;

@Extension
public class ComponentUseChecker extends CFLintScannerAdapter {

    public static enum UsageTypes {
        NEW_COMPONENT,
        CREATEOBJECT,
        ATTRIBUTE,
        CFML_TEMPLATE,
        DI_GET_INSTANCE,
        UNDEFINED
    }

    ;
    static final HashMap<UsageTypes, Pattern> patternsMap;
    static final Pattern PascalCasePattern;

    static {
        patternsMap = new HashMap<>();
        patternsMap.put(UsageTypes.CFML_TEMPLATE, Pattern.compile("template=\"(.*)\""));
        patternsMap.put(UsageTypes.CREATEOBJECT, Pattern.compile("createObject.*component.*[\\'\\\"]([\\w\\d.]*)[\\'\\\"]"));
        patternsMap.put(UsageTypes.ATTRIBUTE, Pattern.compile("[\\'\\\"]([\\w\\.]+)[\\'\\\"]"));
        patternsMap.put(UsageTypes.NEW_COMPONENT, Pattern.compile("new\\s+([\\w\\d.]*)\\(.*"));
        patternsMap.put(UsageTypes.DI_GET_INSTANCE, Pattern.compile("server\\.di\\.getInstance\\([\\'\\\"]([\\w\\d.]*)[\\'\\\"]"));
        PascalCasePattern = Pattern.compile("^[A-Z][a-z]+(?:[A-Z][a-z]+)*$");
    }

    @Override
    public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
        if (expression instanceof CFExpressionStatement) {
            final String code = ((CFExpressionStatement) expression).getExpression().Decompile(0);
            final int lineNo = expression.getLine() + context.startLine() - 1;
            final int offset = expression.getOffset() + context.offset();
            checkComponentUse(context, code, lineNo, offset);
        } else if (expression instanceof CFCompDeclStatement) {
            CFCompDeclStatement compDeclStatement = ((CFCompDeclStatement) expression);
            Map<String, CFExpression> attributes = CFTool.convertMap(compDeclStatement.getAttributes());
            CFExpression extendsOrImplements = null;
            if (attributes.containsKey("extends")) {
                extendsOrImplements = attributes.get("extends");
            } else if (attributes.containsKey("implements"))
                extendsOrImplements = attributes.get("implements");
            if (extendsOrImplements != null) {
                final int lineNo = extendsOrImplements.getLine() + context.startLine() - 1;
                final int offset = compDeclStatement.getOffset() + context.offset();
                checkComponentUse(context, extendsOrImplements.Decompile(0), lineNo, offset, UsageTypes.ATTRIBUTE);
            }
        } else if (expression instanceof CFPropertyStatement) {
            CFPropertyStatement propStatement = (CFPropertyStatement) expression;
            Map<String, CFExpression> attributes = CFTool.convertMap(propStatement.getAttributes());
            CFExpression injectAttribute = null;
            if (attributes.containsKey("inject")) {
                injectAttribute = attributes.get("inject");
            }
            if (injectAttribute != null) {
                final int lineNo = injectAttribute.getLine() + context.startLine() - 1;
                final int offset = injectAttribute.getOffset() + context.offset();
                checkComponentUse(context, injectAttribute.Decompile(0), lineNo, offset, UsageTypes.ATTRIBUTE);
            }
        }

    }

    @Override
    public void element(final Element element, final Context context, final BugList bugs) {
        if (element.getName().equals(CF.CFCOMPONENT)) {
            final String attributeVal = element.getAttributeValue("extends");
            if (attributeVal != null && !attributeVal.trim().isEmpty()) {
                final int lineNo = element.getSource().getRow(element.getBegin());
                verifyComponentUsage(context, lineNo, element.getBegin(), attributeVal);
            }
        } else if (element.getName().equals(CF.CFSET)) {
            final String content = element.getStartTag().getTagContent().toString();
            final int lineNo = element.getSource().getRow(element.getBegin());
            checkComponentUse(context, content, lineNo, element.getBegin());
        }
    }


    private void checkComponentUse(Context context, String code, int lineNo, int offset) {
        UsageTypes componentUsage = getComponentUseIfAny(code);
        if (patternsMap.containsKey(componentUsage)) {
            checkComponentUse(context, code, lineNo, offset, componentUsage);
        }
    }

    private void checkComponentUse(Context context, String code, int lineNo, int offset, UsageTypes componentUsage) {
        Matcher matcher = patternsMap.get(componentUsage).matcher(code);
        if (matcher.find()) {
            String componentName = matcher.group(1);
            verifyComponentUsage(context, lineNo, offset, componentName);
        }
    }

    private void verifyComponentUsage(Context context, int lineNo, int offset, String componentName) {
        if (componentName.contains(".")) {
            String[] paths = componentName.split("\\.");
            componentName = paths[paths.length - 1];
        }
        if (!PascalCasePattern.matcher(componentName).matches()) {
            context.addMessage("INVALID_COMPONENT_USAGE", componentName, lineNo, offset);
        }
    }

    protected UsageTypes getComponentUseIfAny(final String code) {
        String codeLine = code.replaceAll("\\s+", "").toLowerCase();
        if (codeLine.contains("createobject")) {
            return UsageTypes.CREATEOBJECT;
        } else if (codeLine.contains("new")) {
            return UsageTypes.NEW_COMPONENT;
        } else if (codeLine.contains("server.di.getinstance")) {
            return UsageTypes.DI_GET_INSTANCE;
        } else if (codeLine.contains("extends")) {
            return UsageTypes.ATTRIBUTE;
        } else if (codeLine.contains("implements")) {
            return UsageTypes.ATTRIBUTE;
        } else if (codeLine.contains("inject")) {
            return UsageTypes.ATTRIBUTE;
        } else if (codeLine.contains("template")) {
            return UsageTypes.CFML_TEMPLATE;
        } else {
            return UsageTypes.UNDEFINED;
        }
    }

}
