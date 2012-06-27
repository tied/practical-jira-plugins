package com.mycompany.jira.plugins.currency.searchers;

import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.searchers.ExactNumberSearcher;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.FieldVisibilityManager;

/**
 * A custom searcher class that simply reuses an existing searcher.
 */
public class CurrencySearcher extends ExactNumberSearcher {

    public CurrencySearcher(final FieldVisibilityManager fieldVisibilityManager,
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

}
