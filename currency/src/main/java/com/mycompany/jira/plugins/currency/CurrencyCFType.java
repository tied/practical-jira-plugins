 package com.mycompany.jira.plugins.currency;

import com.mycompany.jira.plugins.currency.searchers.CurrencyCustomFieldIndexer;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import java.util.Currency;
import java.util.Map;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;

public class CurrencyCFType extends NumberCFType {

    public CurrencyCFType(final CustomFieldValuePersister customFieldValuePersister, 
                          final DoubleConverter doubleConverter, 
                          final GenericConfigManager genericConfigManager) {
        super(customFieldValuePersister,
              doubleConverter,
              genericConfigManager);
    }

    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue, 
                                                     final CustomField field, 
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);
        String symbol = Currency.getInstance(getI18nBean().getLocale()).getSymbol();

        map.put("currencySymbol", symbol);
        log.debug("Currency symbol is " + symbol);
        return map;
    }

    /**
     * Handle the optional M and K suffixes.
     *
     * Note that in JIRA 4.x this method returned an Object.
     */
    public Double getSingularObjectFromString(String numberString)  
        throws FieldValidationException {
        log.debug("getSingularObjectFromString: " + numberString);

        // This is for the advanced currency searcher
        if (numberString.equalsIgnoreCase(CurrencyCustomFieldIndexer.SMALL)) {
            return Double.valueOf("0");
        }

        double multiplier = 1.0;
        if (numberString.endsWith("K") || numberString.endsWith("k")) {
            multiplier = 1000.0;
        }
        if (numberString.endsWith("M") || numberString.endsWith("m")) {
            multiplier = 1000000.0;
        }

        // Remove the multiplier character
        if (multiplier > 1.0) {
            numberString = numberString.substring(0, numberString.length() - 1);
        }

        // Use the parent's method to get the actual value of the field
        // Prior to JIRA 5.0 this needed a cast
        Double value = super.getSingularObjectFromString(numberString);
        // Adjust the value appropriately
        value = value * multiplier;

        // Copied from DoubleConverterImpl where this is private
        Double MAX_VALUE = new Double("100000000000000"); // 14 zeroes
        if (value.compareTo(MAX_VALUE) > 0) {
                throw new FieldValidationException("The value " + 
numberString + " is larger than the maximum value allowed:  " + MAX_VALUE);
        }

        return value;
    }

}
