package com.mycompany.jira.plugins.multiple;

import org.apache.commons.lang.StringUtils;
import com.atlassian.core.util.collection.EasyList;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.AbstractCustomFieldType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.util.velocity.NumberTool;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.apache.log4j.Logger;

import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NotNullPredicate;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;

/**
 * All the other Multi* classes refer to Users or Options. This class,
 * like VersionCFType, uses a different transport object, a Collection
 * of Carrier objects.
 */
public class MultipleValuesCFType extends AbstractCustomFieldType {

    public static final Logger log = Logger.getLogger(MultipleValuesCFType.class);

    private final CustomFieldValuePersister persister;
    private final GenericConfigManager genericConfigManager;

    // The type of data in the database, one entry per value in this field
    private static final PersistenceFieldType DB_TYPE = PersistenceFieldType.TYPE_UNLIMITED_TEXT;
    
    /**
     * Used in the database representation of a singular value.
     * Treated as a regex when checking text input.
     */
    public static final String DB_SEP = "###";

    public MultipleValuesCFType(final CustomFieldValuePersister customFieldValuePersister,
                                final GenericConfigManager genericConfigManager) {
        this.persister = customFieldValuePersister;
        this.genericConfigManager = genericConfigManager;
    }

    /**
     * Convert a database representation of a Carrier object into 
     * a Carrier object. This method is also used for bulk moves and imports.
     */
    public Object getSingularObjectFromString(String dbValue)  
        throws FieldValidationException {
        log.debug("getSingularObjectFromString: " + dbValue);
        if (StringUtils.isEmpty(dbValue)) {
            return null;
        }
        String[] parts = dbValue.split(DB_SEP);
        if (parts.length == 0 || parts.length > 2) {
            log.warn("Invalid database value for MultipleValuesCFType ignored: " + dbValue);
            // If this should not be allowed, then throw a
            // FieldValidationException instead
            return null;
        }
        Double d = new Double(parts[0]);
        String s = "";
        if (parts.length == 2) {
            s = parts[1];
        }
        return new Carrier(d, s);
    }

    public Object getValueFromIssue(CustomField field,
                                    Issue issue) {
        // This is also called to display a default value in view.vm
        // in which case the issue is a dummy one with no key
        if (issue == null || issue.getKey() == null) {
            log.debug("getValueFromIssue was called with a dummy issue for default");
            return null;
        }

        // These are the database representation of the singular objects
        final List<String> values = persister.getValues(field, issue.getId(), DB_TYPE);
        log.debug("getValueFromIssue entered with " + values);
        if ((values != null) && !values.isEmpty()) {
            List<Carrier> result = new ArrayList<Carrier>();
            for (Iterator it = values.iterator(); it.hasNext(); ) {
                String dbValue = (String)it.next();
                Carrier carrier = (Carrier)getSingularObjectFromString(dbValue);
                if (carrier == null) {
                    continue;
                }
                result.add(carrier);
            }
            return result;
        } else {
            return null;
        }
    }

    public void createValue(CustomField field, Issue issue, Object value) {
        if (value instanceof Collection)
        {
            persister.createValues(field, issue.getId(), DB_TYPE, getDbValueFromCollection(value));
        }
        else
        {
            persister.createValues(field, issue.getId(), DB_TYPE, getDbValueFromCollection(EasyList.build(value)));
        }
    }

    public void updateValue(CustomField field, Issue issue, Object value) {
        persister.updateValues(field, issue.getId(), DB_TYPE, getDbValueFromCollection(value));
    }

    /**
     * For removing the field, not for removing one value
     */
    public Set<Long> remove(CustomField field) {
        return persister.removeAllValues(field.getId());
    }

    /**
     * Convert a transport object (a Collection of Carrier objects) to
     * its database representation and store it in the database.
     */
    public void setDefaultValue(FieldConfig fieldConfig, Object value) {
        log.debug("setDefaultValue with object " + value);
        Collection carrierStrings = getDbValueFromCollection(value);
        if (carrierStrings != null) {
            carrierStrings = new ArrayList(carrierStrings);
            genericConfigManager.update(CustomFieldType.DEFAULT_VALUE_TYPE, fieldConfig.getId().toString(), carrierStrings);
        }
    }

    /**
     * Retrieve the stored default value (if any) from the database
     * and convert it to a transport object (a Collection of Carrier
     * objects).
     */
    public Object getDefaultValue(FieldConfig fieldConfig) {
        final Object o = genericConfigManager.retrieve(CustomFieldType.DEFAULT_VALUE_TYPE, fieldConfig.getId().toString());
        log.debug("getDefaultValue with database value " + o);

        Collection<Carrier> collectionOfCarriers = null;
        if (o instanceof Collection) {
            collectionOfCarriers = (Collection) o;
        } else if (o instanceof Carrier) {
            log.warn("Backwards compatible default value, should not occur");
            collectionOfCarriers = EasyList.build(o);
        }

        if (collectionOfCarriers == null) {
            return null; // No default value exists
        }

        final Collection collection = CollectionUtils.collect(collectionOfCarriers, new Transformer() {
            // Convert a database value (String) to a singular Object (Carrier)
            public Object transform(final Object input) {
                if (input == null) {
                    return null;
                }
                String dbValue = (String)input;
                return (Carrier)getSingularObjectFromString(dbValue);
            }
        });
        CollectionUtils.filter(collection, NotNullPredicate.getInstance());
        log.debug("getDefaultValue returning " + collection);
        return collection;
    }

    /**
     * Validate the input from the web pages, a Collection of Strings.
     * Exceptions raised later on after this has passed appear as an
     * ugly page.
     */
    public void validateFromParams(CustomFieldParams relevantParams, 
                                   ErrorCollection errorCollectionToAddTo, 
                                   FieldConfig config) {
        log.debug("validateFromParams: " + relevantParams.getKeysAndValues());
        try {
            getValueFromCustomFieldParams(relevantParams);
        } catch (FieldValidationException fve) {
            errorCollectionToAddTo.addError(config.getCustomField().getId(), fve.getMessage());
        }
    }

    /**
     * Extract a transport object from the string parameters,
     * Clearing an amount removes the row.
     */
    public Object getValueFromCustomFieldParams(CustomFieldParams parameters) 
        throws FieldValidationException {
        log.debug("getValueFromCustomFieldParams: " + parameters.getKeysAndValues());
        // Strings in the order they appeared in the web page
        final Collection values = parameters.getAllValues();
        if ((values != null) && !values.isEmpty()) {
            Collection<Carrier> value = new ArrayList();
            for (Iterator it = values.iterator(); it.hasNext(); ) {
                String dStr = (String)it.next();
                String s = (String)it.next();
                // Allow empty text but not empty amounts
                if (dStr == null || dStr.equals("")) {
                    log.debug("Ignoring text " + s + " because the amount is empty");
                    // This is used to delete a row so do not throw a
                    // FieldValidationException
                    continue;
                }
                if (s == null) {
                    s = "";
                }
                // Make sure the value can be stored safely later on
                s = s.replace(DB_SEP, "");

                try {
                    Double d = new Double(dStr);
                    value.add(new Carrier(d, s));
                } catch (NumberFormatException nfe) {
                    // A value was provided but it was an invalid value
                    throw new FieldValidationException(dStr + " is not a number");
                }
            }
            return value;
        } else {
            return null;
        }
    }
    
    /**
     * This method is used to create the $value object in Velocity templates.
     */
    public Object getStringValueFromCustomFieldParams(CustomFieldParams parameters) {
        log.debug("getStringValueFromCustomFieldParams: " + parameters.getKeysAndValues());
        return parameters.getAllValues();
    }

    public String getStringFromSingularObject(Object singularObject) {
        assertObjectImplementsType(Carrier.class, singularObject);
        Carrier carrier = (Carrier)singularObject;
        return carrier.toString();
    }

    public String getChangelogValue(CustomField field, Object value)  {
        if (value == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Collection<Carrier> carriers = (Collection<Carrier>) value;
        for (Carrier carrier: carriers) {
            sb.append(carrier.toString()); 
            // Newlines are text not HTML here
            sb.append(", ");
                        
        }
        return sb.toString();
    }

    // Helper Methods

    /**
     * Convert the Transport object to a collection of the
     * representation used in the database.
     */
    private Collection getDbValueFromCollection(final Object value)
    {
        log.debug("getDbValueFromCollection: " + value);
        if (value == null) {
            return Collections.EMPTY_LIST;
        }
        Collection<Carrier> carriers = (Collection<Carrier>) value;
        List<String> result = new ArrayList<String>();
        for (Carrier carrier : carriers) {
            if (carrier == null) {
                continue;
            }
            StringBuffer sb = new StringBuffer();
            sb.append(carrier.getAmount().toString());
            sb.append(DB_SEP);
            sb.append(carrier.getNote());
            result.add(sb.toString());
        }
        return result;
    }

}
