package com.mycompany.jira.plugins.currency;

import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.util.velocity.NumberTool;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public class CurrencyCFType extends NumberCFType {

    public CurrencyCFType(final CustomFieldValuePersister customFieldValuePersister, 
                          final DoubleConverter doubleConverter, 
                          final GenericConfigManager genericConfigManager) {
        super(customFieldValuePersister,
              doubleConverter,
              genericConfigManager);
    }

    /**
     * This is where the different aspects of a custom field such as 
     * having a default value or other config items are set.
     */
    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        final List<FieldConfigItemType> configurationItemTypes = super.getConfigurationItemTypes();
        configurationItemTypes.add(new CurrencyConfigItem());
        return configurationItemTypes;
    }

    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue, 
                                                     final CustomField field, 
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        // This method is also called to get the default value, in
        // which case issue is null so we can't use it to add currencyLocale
        if (issue == null) {
            return map;
        }

         FieldConfig fieldConfig = field.getRelevantConfig(issue);
         // Get the stored configuration choice
         map.put("currencyLocale", DAO.getCurrentLocale(fieldConfig));

        return map;
    }

}
