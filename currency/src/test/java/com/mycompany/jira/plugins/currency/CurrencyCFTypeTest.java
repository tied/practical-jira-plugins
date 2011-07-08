package com.mycompany.jira.plugins.currency;

import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.converters.DoubleConverterImpl;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.util.I18nHelper;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

/**
 * Unit tests for the CurrencyCFType custom field type.
 */
public class CurrencyCFTypeTest {

    @Mock private CustomFieldValuePersister customFieldValuePersister;
    @Mock private JiraAuthenticationContext jiraAuthenticationContext;
    @Mock private GenericConfigManager genericConfigManager;

    @Mock private Issue issue;
    @Mock private CustomField customField;
    @Mock private I18nHelper i18nHelper;
    @Mock private FieldLayoutItem fieldLayoutItem;

    private DoubleConverter doubleConverter;

    @Before 
    public void setUp() { 
        // Create all the mock objects marked with @Mock
        MockitoAnnotations.initMocks(this);
        doubleConverter = new DoubleConverterImpl(jiraAuthenticationContext);

        // This is needed by DoubleConverter getDisplayFormat
        when(jiraAuthenticationContext.getI18nHelper()).thenReturn(i18nHelper);
        when(i18nHelper.getLocale()).thenReturn(Locale.getDefault());
    }

    @Test
    public void testGetSingularObjectFromString() {
        CurrencyCFType currencyCFType = new CurrencyCFType(customFieldValuePersister,
                                                           doubleConverter,
                                                           genericConfigManager);
        assertTrue(currencyCFType.getSingularObjectFromString("10.0").equals(new Double(10.0)));
        assertTrue(currencyCFType.getSingularObjectFromString("10").equals(new Double(10.0)));
        assertTrue(currencyCFType.getSingularObjectFromString("0").equals(new Double(0)));
        assertTrue(currencyCFType.getSingularObjectFromString("-1").equals(new Double(-1)));

        // Test the scaling suffixes too
        assertTrue(currencyCFType.getSingularObjectFromString("10k").equals(new Double(10*1000)));
        assertTrue(currencyCFType.getSingularObjectFromString("10m").equals(new Double(10*1000*1000)));
    }

    //@Test
    public void testVelocityParameters() {
        CurrencyCFType currencyCFType = new CurrencyCFType(customFieldValuePersister,
                                                           doubleConverter,
                                                           genericConfigManager);
        // This requires a servlet context for ComponentManager and
        // getI18nBean to work. Plain Mockito doesn't handle that well
        // but Power Mockito does. For more details see
        // https://www.adaptavist.com/display/~jmcgivern/2010/05/09/Unit+Testing+JIRA+with+PowerMock+for+Mockito
        //Map<String, Object> velocityParams = currencyCFType.getVelocityParameters(issue, customField, fieldLayoutItem);
        //assertNotNull(velocityParams.get("currencySymbol"));
    }

    @After 
    public void tearDown() { 
    }

}
