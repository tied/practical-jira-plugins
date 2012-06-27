package com.mycompany.jira.plugins.currency.searchers;

import com.atlassian.jira.issue.index.indexers.impl.AbstractCustomFieldIndexer;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.fields.CustomField;
import static com.atlassian.jira.util.dbc.Assertions.notNull;
import com.atlassian.jira.web.FieldVisibilityManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * A custom field indexer for Currency custom fields
 */
public class CurrencyCustomFieldIndexer extends AbstractCustomFieldIndexer {

    private final CustomField customField;
    private final DoubleConverter doubleConverter;

    public final static String SMALL = "Small";
    
    public CurrencyCustomFieldIndexer(final FieldVisibilityManager fieldVisibilityManager, 
                                      final CustomField customField,
                                      final DoubleConverter doubleConverter) {
        super(fieldVisibilityManager, notNull("field", customField));
        this.doubleConverter = notNull("doubleConverter", doubleConverter);
        this.customField = customField;
    }

    /**
     * This method is called when updating the index for a custom field that
     * is visible in the given issue. The value is added verbatim to the index.
     */
    public void addDocumentFieldsSearchable(final Document doc, 
                                            final Issue issue) {
        addDocumentFields(doc, issue, Field.Index.NOT_ANALYZED);
    }

    /**
     * This method is called when updating the index for a custom field that
     * is not visible in the given issue. The value is added but
     * marked as not present in this issue.
     *
     */
    public void addDocumentFieldsNotSearchable(final Document doc, 
                                               final Issue issue) {
        addDocumentFields(doc, issue, Field.Index.NO);
    }

    /**
     * Add an entry to a Lucene index.
     * 
     * @param doc The Lucene Document object to be updated
     * @param issue The JIRA issue that contains the field we're searching
     * @param indexType How the data is added to the Lucene index
     */
    private void addDocumentFields(final Document doc, 
                                   final Issue issue, 
                                   final Field.Index indexType) {
        /*
          getValue() calls the CustomFieldType getValueFromIssue method
          so this Object is the custom field's transport object. In this
          case that is a Double.
        */
        Object value = customField.getValue(issue);
        if (value == null) {
            return;
        }
        Double dbl = (Double)value;

        // JIRA's Lucene indexes use Strings but not
        // necessarily the same as what
        // getStringFromSingularObject(value)
        // returns. Numbers are carefully formatted to use a
        // single format.
        final String stringValue = doubleConverter.getStringForLucene(dbl);
        Field field = new Field(getDocumentFieldId(), 
                                stringValue, 
                                Field.Store.YES, 
                                indexType);
        doc.add(field);
        
        // Add the extra information to the index
        if (dbl.doubleValue() < 10.0) {
            field = new Field(getDocumentFieldId(), 
                              SMALL,
                              Field.Store.YES, 
                              indexType);
            doc.add(field);
        }
    }
}
