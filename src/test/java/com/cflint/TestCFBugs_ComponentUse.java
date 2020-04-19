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
    public void testNewComponent() throws CFLintScanException {
        final String tagSrc = "config = new components.configuration.manager('VoucherEngine', Attributes);";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testExtendsComponent() throws CFLintScanException {
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
    public void testImplementsComponent() throws CFLintScanException {
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
    public void testImplementsComponentWithFolderPath() throws CFLintScanException {
        final String tagSrc = "component implements = 'MyProject.interface.handler' {}";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testCreateObject() throws CFLintScanException {
        final String tagSrc = "server.di = createObject('component', 'ioc.di').init();";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

    @Test
    public void testPropertyInject() throws CFLintScanException {
        final String tagSrc = "property name = 'reporter' inject = 'components.error.errorfunctions';";
        CFLintResult lintresult = cfBugs.scan(tagSrc, "NicComponentNm.cfc");
        Collection<List<BugInfo>> result = lintresult.getIssues().values();
        assertEquals(lintresult.getIssues().values().toString(), 1, result.size());
    }

}
