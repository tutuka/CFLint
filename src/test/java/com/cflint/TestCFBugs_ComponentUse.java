package com.cflint;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import com.cflint.plugins.core.ComponentPath;
import org.junit.Before;
import org.junit.Test;

import com.cflint.api.CFLintAPI;
import com.cflint.api.CFLintResult;
import com.cflint.config.ConfigBuilder;
import com.cflint.exception.CFLintScanException;

public class TestCFBugs_ComponentUse {

    private CFLintAPI cfBugs;
    private String basePath = "src/test/resources/com/cflint/componentusagecheck/";

    @Before
    public void setUp() throws Exception {
        final ConfigBuilder configBuilder = new ConfigBuilder().include("INVALID_COMPONENT_USAGE");
        cfBugs = new CFLintAPI(configBuilder.build());
        ComponentPath rootPath = ComponentPath.getInstance(basePath + "api/ura/Application.cfc");
    }

    @Test
    public void testNewComponentError() throws CFLintScanException {
        final String tagSrc = "config = new components.configuration.handler('VoucherEngine', Attributes);";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "ComponentName.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testNewComponentNoError() throws CFLintScanException {
        final String tagSrc = "config = new components.configuration.Manager('VoucherEngine', Attributes);";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "ComponentName.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testExtendsComponentError() throws CFLintScanException {
        final String tagSrc = "component extends = 'base' restpath = 'authenticate' rest = true {\n" +
            " public function init() {\n" +
            "      return this;\n" +
            "  }\n" +
            "}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, basePath + "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testExtendsComponentNoError() throws CFLintScanException {
        final String tagSrc = "component extends = 'api.Base' restpath = 'authenticate' rest = true {\n" +
            " public function init() {\n" +
            "      return this;\n" +
            "  }\n" +
            "}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }


    @Test
    public void testImplementsComponentError() throws CFLintScanException {
        final String tagSrc = "component implements = 'base' restpath = 'authenticate' rest = true {\n" +
            " public function init() {\n" +
            "      return this;\n" +
            "  }\n" +
            "}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, basePath + "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testImplementsComponentNoError() throws CFLintScanException {
        final String tagSrc = "component implements = 'api.Base' restpath = 'authenticate' rest = true {\n" +
            " public function init() {\n" +
            "      return this;\n" +
            "  }\n" +
            "}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testImplementsComponentErrorWithFolderPath() throws CFLintScanException {
        final String tagSrc = "component implements = 'components.configuration.handler' {}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testImplementsComponentNoErrorWithFolderPath() throws CFLintScanException {
        final String tagSrc = "component implements = 'components.configuration.Manager' {}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testCreateObjectError() throws CFLintScanException {
        final String tagSrc = "server.di = createObject('component', 'ioc.di').init();";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testCreateObjectNoError() throws CFLintScanException {
        final String tagSrc = "server.di = createObject('component', 'components.XmlRpc').init();";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }


    @Test
    public void testPropertyInjectError() throws CFLintScanException {
        final String tagSrc = "property name = 'reporter' inject = 'components.configuration.handler';";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testPropertyInjectNoError() throws CFLintScanException {
        final String tagSrc = "property name = 'reporter' inject = 'components.configuration.Manager';";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testExtendsCFMLComponentError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent extends=\"components.configuration.handler\">\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testExtendsCFMLComponentNoError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent extends=\"components.XmlRpc\">\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testCFMLCreateObjectError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset errorHandler = createObject(\"component\",\"components.configuration.handler\")>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testCFMLCreateObjectNoError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset errorHandler = createObject(\"component\",\"components.configuration.Manager\")>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testCFMLNewComponentError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset maskedVoucherNumberService = new components.configuration.handler()>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testCFMLNewComponentNoError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset maskedVoucherNumberService = new components.configuration.Manager()>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testDiGetInstanceError() throws CFLintScanException {
        final String tagSrc = "exporter = server.di.getInstance('components.configuration.handler');";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testDiGetInstanceNoError() throws CFLintScanException {
        final String tagSrc = "exporter = server.di.getInstance('components.configuration.Manager');";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }


}
