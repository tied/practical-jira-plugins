package com.mycompany.jira.plugins.currency.searchers;

import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.util.NumberIndexValueConverter;

/**
 * Converts a query literal into a number index representation.
 *
 * @since v4.0
 */
public class CurrencyIndexValueConverter extends NumberIndexValueConverter
{
    private final DoubleConverter doubleConverter;

    public CurrencyIndexValueConverter(DoubleConverter doubleConverter)
    {
        super(doubleConverter);
        this.doubleConverter = doubleConverter;
    }

    public String convertToIndexValue(final QueryLiteral rawValue)
    {
        if (rawValue.isEmpty())
        {
            return null;
        }
        
        try
        {
            String rawString = rawValue.asString();
            if (rawString.equalsIgnoreCase(CurrencyCustomFieldIndexer.SMALL)) {
                return CurrencyCustomFieldIndexer.SMALL;
            }
            return doubleConverter.getStringForLucene(rawString);
        }
        catch (FieldValidationException e)
        {
            return null;
        }
    }
}
