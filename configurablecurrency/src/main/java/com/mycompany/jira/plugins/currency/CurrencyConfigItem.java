package com.mycompany.jira.plugins.currency;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import java.util.Currency;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConfigItem implements FieldConfigItemType {

    // The name of this kind of configuration, as seen in the field
    // configuration scheme
    public String getDisplayName() {
        return "Currency";
    }

    // This is the text seen in the field configuration screen
    public String getDisplayNameKey() {
        return "Selected Currency";
    }
    
    // This is the current value as shown in the field configuration screen
    public String getViewHtml(FieldConfig fieldConfig, 
                              FieldLayoutItem fieldLayoutItem) {
        Locale locale = DAO.getCurrentLocale(fieldConfig);
        return DAO.getDisplayValue(locale);
    }

    // The unique identifier for this kind of configuration, and the
    // also key for the configs Map in edit.vm
    public String getObjectKey() {
        return "currencyconfig";
    }

    // Return the Object in the Velocity edit context in $configs
    public Object getConfigurationObject(Issue issue, FieldConfig config) {
        Map result = new HashMap();
        result.put("currencyLocale", DAO.getCurrentLocale(config));
        result.put("currencySymbol", DAO.getCurrentSymbol(config));
        return result;
    }

    // Where the Edit link should redirect to when it's clicked on
    public String getBaseEditUrl() {
        return "EditCurrencyConfig.jspa";
    }

}