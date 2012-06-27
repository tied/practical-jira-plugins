package com.mycompany.jira.plugins.currency.searchers;

import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.searchers.ExactNumberSearcher;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.issue.customfields.statistics.CustomFieldStattable;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.statistics.NumericFieldStatisticsMapper;
import com.atlassian.jira.issue.statistics.StatisticsMapper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.plugin.customfield.CustomFieldSearcherModuleDescriptor;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.FieldVisibilityManager;
import org.apache.log4j.Logger;

/**
 * A searcher class that reuses an existing searcher.
 */
public class CurrencyStattableSearcher extends ExactNumberSearcher implements CustomFieldStattable {

    public static final Logger log = Logger.getLogger(CurrencyStattableSearcher.class);

    public CurrencyStattableSearcher(final FieldVisibilityManager fieldVisibilityManager,
                                     final JqlOperandResolver jqlOperandResolver,
                                     final DoubleConverter doubleConverter, 
                                     final CustomFieldInputHelper customFieldInputHelper,
                                     final I18nHelper.BeanFactory beanFactory) {
        super(fieldVisibilityManager,
              jqlOperandResolver,
              doubleConverter,
              customFieldInputHelper,
              beanFactory);
    }
    
    public StatisticsMapper getStatisticsMapper(final CustomField customField) {
        return new NumericFieldStatisticsMapper(customField.getId());
    }

}
