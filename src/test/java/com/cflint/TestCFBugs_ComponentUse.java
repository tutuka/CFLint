package com.cflint;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cflint.api.CFLintAPI;
import com.cflint.api.CFLintResult;
import com.cflint.config.ConfigBuilder;
import com.cflint.exception.CFLintScanException;

public class TestCFBugs_ComponentUse {

    private CFLintAPI cfBugs;

    @Before
    public void setUp() throws Exception {
        final ConfigBuilder configBuilder = new ConfigBuilder().include("INVALID_COMPONENT_USAGE");
        cfBugs = new CFLintAPI(configBuilder.build());
    }

    @Test
    public void testNewComponentError() throws CFLintScanException {
        final String tagSrc = "config = new components.configuration.manager('VoucherEngine', Attributes);";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testNewComponentNoError() throws CFLintScanException {
        final String tagSrc = "config = new components.configuration.Manager('VoucherEngine', Attributes);";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
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
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testExtendsComponentNoError() throws CFLintScanException {
        final String tagSrc = "component extends = 'Base' restpath = 'authenticate' rest = true {\n" +
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
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testImplementsComponentNoError() throws CFLintScanException {
        final String tagSrc = "component implements = 'Base' restpath = 'authenticate' rest = true {\n" +
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
        final String tagSrc = "component implements = 'MyProject.interface.handler' {}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testImplementsComponentNoErrorWithFolderPath() throws CFLintScanException {
        final String tagSrc = "component implements = 'MyProject.interface.Handler' {}";
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
        final String tagSrc = "property name = 'reporter' inject = 'components.error.errorfunctions';";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testPropertyInjectNoError() throws CFLintScanException {
        final String tagSrc = "property name = 'reporter' inject = 'components.error.Errorfunctions';";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testExtendsCFMLComponentError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent extends=\"components.cfcs.xmlRpcService\">\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testExtendsCFMLComponentNoError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent extends=\"components.cfcs.XmlRpcService\">\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testCFMLCreateObjectError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset errorHandler = createObject(\"component\",\"components.error.errorFunctions\")>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testCFMLCreateObjectNoError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset errorHandler = createObject(\"component\",\"components.error.ErrorFunctions\")>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testCFMLNewComponentError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset maskedVoucherNumberService = new components.cfcs.maskVoucherNumber()>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testCFMLNewComponentNoError() throws CFLintScanException {
        final String tagSrc = "<cfcomponent >\n" +
            "<cfset maskedVoucherNumberService = new components.cfcs.MaskVoucherNumber()>\n" +
            "</cfcomponent>";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }

    @Test
    public void testDiGetInstanceError() throws CFLintScanException {
        final String tagSrc = "exporter = server.di.getInstance('components.eftBatchProcessor.service.secExporter');";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testDiGetInstanceNoError() throws CFLintScanException {
        final String tagSrc = "exporter = server.di.getInstance('components.eftBatchProcessor.service.SecExporter');";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 0, result.size());
    }


}
