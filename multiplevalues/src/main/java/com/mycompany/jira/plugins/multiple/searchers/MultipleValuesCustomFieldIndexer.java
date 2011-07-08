package com.mycompany.jira.plugins.multiple.searchers;

import com.mycompany.jira.plugins.multiple.MultipleValuesCFType;
import com.mycompany.jira.plugins.multiple.Carrier;

import com.atlassian.jira.issue.index.indexers.impl.*;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import static com.atlassian.jira.util.dbc.Assertions.notNull;
import com.atlassian.jira.web.FieldVisibilityManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.log4j.Logger;
import java.util.Collection;

/**
 * A custom field indexer for the multiple values custom field example.
 *
 * @since v4.0
 */
public class MultipleValuesCustomFieldIndexer extends AbstractCustomFieldIndexer
{
    public static final Logger log = Logger.getLogger(MultipleValuesCFType.class);
    private final CustomField field;

    public MultipleValuesCustomFieldIndexer(final FieldVisibilityManager fieldVisibilityManager, final CustomField customField)
    {
        super(fieldVisibilityManager, notNull("field", customField));
        this.field = customField;
    }

    public void addDocumentFieldsSearchable(final Document doc, final Issue issue)
    {
        addDocumentFields(doc, issue, Field.Index.UN_TOKENIZED);
    }

    public void addDocumentFieldsNotSearchable(final Document doc, final Issue issue)
    {
        addDocumentFields(doc, issue, Field.Index.NO);
    }

    private void addDocumentFields(final Document doc, final Issue issue, final Field.Index indexType)
    {
        Object value = field.getValue(issue);
        if (value != null)
        {
	    // This is the transport object, a Collection of Carrier objects
	    log.debug("addDocumentFields with value " + value);
	    Collection<Carrier> carriers = (Collection<Carrier>)value;
	    for (Carrier carrier: carriers) {
		String amountStr = carrier.getAmount().toString();
		String noteStr = carrier.getNote();
		// TODO should the ids have a suffix per field?
		if (amountStr != null && !amountStr.equals("")) {
		    doc.add(new Field(getDocumentFieldId(), amountStr, Field.Store.YES, indexType));
		}
		if (noteStr != null && !noteStr.equals("")) {
		    doc.add(new Field(getDocumentFieldId(), noteStr, Field.Store.YES, indexType));
		}
	    }
        }
    }
}