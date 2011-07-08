package com.mycompany.jira.plugins.currency.searchers;

import com.atlassian.jira.issue.customfields.searchers.*;

import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldValueProvider;
import com.atlassian.jira.issue.customfields.SingleValueCustomFieldValueProvider;
import com.atlassian.jira.issue.customfields.SortableCustomFieldSearcher;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.searchers.information.CustomFieldSearcherInformation;
import com.atlassian.jira.issue.customfields.searchers.renderer.CustomFieldRenderer;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.issue.customfields.searchers.transformer.ExactNumberCustomFieldSearchInputTransformer;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.index.indexers.FieldIndexer;
import com.atlassian.jira.issue.search.ClauseNames;
import com.atlassian.jira.issue.search.LuceneFieldSorter;
import com.atlassian.jira.issue.search.searchers.information.SearcherInformation;
import com.atlassian.jira.issue.search.searchers.renderer.SearchRenderer;
import com.atlassian.jira.issue.search.searchers.transformer.SearchInputTransformer;
import com.atlassian.jira.issue.statistics.NumericFieldStatisticsMapper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.jql.operator.OperatorClasses;
import com.atlassian.jira.jql.query.ActualValueCustomFieldClauseQueryFactory;
import com.atlassian.jira.jql.util.NumberIndexValueConverter;
import com.atlassian.jira.jql.validator.NumberCustomFieldValidator;
import static com.atlassian.jira.util.dbc.Assertions.notNull;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.jira.JiraDataTypes;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class CurrencyAdvancedSearcher extends AbstractInitializationCustomFieldSearcher
        implements CustomFieldSearcher, SortableCustomFieldSearcher
{
    private final FieldVisibilityManager fieldVisibilityManager;
    private final JqlOperandResolver jqlOperandResolver;
    private final DoubleConverter doubleConverter;
    private final CustomFieldInputHelper customFieldInputHelper;

    private volatile CustomFieldSearcherInformation searcherInformation;
    private volatile SearchInputTransformer searchInputTransformer;
    private volatile SearchRenderer searchRenderer;
    private volatile CustomFieldSearcherClauseHandler customFieldSearcherClauseHandler;

    public CurrencyAdvancedSearcher(final FieldVisibilityManager fieldVisibilityManager, final JqlOperandResolver jqlOperandResolver,
            final DoubleConverter doubleConverter, final CustomFieldInputHelper customFieldInputHelper)
    {
        this.fieldVisibilityManager = fieldVisibilityManager;
        this.jqlOperandResolver = jqlOperandResolver;
        this.doubleConverter = doubleConverter;
        this.customFieldInputHelper = notNull("customFieldInputHelper", customFieldInputHelper);
    }

    /**
     * This is the first time the searcher knows what its ID and names are
     *
     * @param field the Custom Field for this searcher
     */
    public void init(CustomField field)
    {
        // 1. Create the SearcherInformation object
        // This inserts the new value "Small" into the Lucene index
        final FieldIndexer indexer = new CurrencyCustomFieldIndexer(fieldVisibilityManager, field, doubleConverter);
        // This shouldn't need to change for different searchers
        this.searcherInformation = new CustomFieldSearcherInformation(field.getId(), field.getNameKey(), Collections.<FieldIndexer>singletonList(indexer), new AtomicReference<CustomField>(field));

        // 2. Create the SearchInputTransformer object. See that 
        // interface for more information.
        final ClauseNames names = field.getClauseNames();
        this.searchInputTransformer = new ExactNumberCustomFieldSearchInputTransformer(field, names, searcherInformation.getId(), customFieldInputHelper);

        // 3. Create the SearchRenderer object
        final CustomFieldValueProvider customFieldValueProvider = new SingleValueCustomFieldValueProvider();
        this.searchRenderer = new CustomFieldRenderer(names, getDescriptor(), field, customFieldValueProvider, fieldVisibilityManager);

        // 4. Create the clause handler.
        // This converts what comes from the web page to the String that is
        // searched for in the Lucene index
        // This class is harder to change because
        // com.atlassian.jira.jql.validator.IndexValuesValidator is
        // not a public class.
        final CurrencyIndexValueConverter indexValueConverter = new CurrencyIndexValueConverter(doubleConverter);
        this.customFieldSearcherClauseHandler = new SimpleCustomFieldSearcherClauseHandler(
           new NumberCustomFieldValidator(jqlOperandResolver, indexValueConverter),
           new ActualValueCustomFieldClauseQueryFactory(field.getId(), jqlOperandResolver, indexValueConverter, true), 
           OperatorClasses.EQUALITY_AND_RELATIONAL_WITH_EMPTY,
           JiraDataTypes.NUMBER);
    }

    public SearcherInformation<CustomField> getSearchInformation()
    {
        if (searcherInformation == null)
        {
            throw new IllegalStateException("Attempt to retrieve SearcherInformation off uninitialised custom field searcher.");
        }
        return searcherInformation;
    }

    public SearchInputTransformer getSearchInputTransformer()
    {
        if (searchInputTransformer == null)
        {
            throw new IllegalStateException("Attempt to retrieve searchInputTransformer off uninitialised custom field searcher.");
        }
        return searchInputTransformer;
    }

    public SearchRenderer getSearchRenderer()
    {
        if (searchRenderer == null)
        {
            throw new IllegalStateException("Attempt to retrieve searchRenderer off uninitialised custom field searcher.");
        }
        return searchRenderer;
    }

    public CustomFieldSearcherClauseHandler getCustomFieldSearcherClauseHandler()
    {
        if (customFieldSearcherClauseHandler == null)
        {
            throw new IllegalStateException("Attempt to retrieve customFieldSearcherClauseHandler off uninitialised custom field searcher.");
        }
        return customFieldSearcherClauseHandler;
    }

    public LuceneFieldSorter getSorter(CustomField customField)
    {
        return new NumericFieldStatisticsMapper(customField.getId());
    }
}