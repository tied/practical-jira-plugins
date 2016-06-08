package com.pduleba.jira.plugins.fields.currency;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

public class CurrencyCustomFieldConfiguration implements FieldConfigItemType {

    // The name of this kind of configuration, as seen in the field
    // configuration scheme
    public String getDisplayName() {
        return "Currency";
    }

    // This is the text shown in the field configuration screen
    // or an i18n key for the text
    public String getDisplayNameKey() {
        return "Selected Currency";
    }
    
    // This is the current value as shown in the field configuration screen
    public String getViewHtml(FieldConfig fieldConfig, 
                              FieldLayoutItem fieldLayoutItem) {
        Locale locale = DAO.getCurrentLocale(fieldConfig);
        return DAO.getDisplayValue(locale);
    }

    // The unique identifier for this kind of configuration, and also the
    // key for the $configs Map used in edit.vm
    public String getObjectKey() {
        return "currencyconfig";
    }

    // Return the Object used in the Velocity edit context in $configs
    public Object getConfigurationObject(Issue issue, FieldConfig config) {
        Map<String, Object> result = new HashMap<>();
        result.put("currencyLocale", DAO.getCurrentLocale(config));
        result.put("currencySymbol", DAO.getCurrentSymbol(config));
        return result;
    }

    // Where the Edit link should redirect to when it's clicked on
    public String getBaseEditUrl() {
        return "EditConfigurationAction.jspa";
    }

}