package com.mycompany.jira.plugins.validators;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.workflow.WorkflowFunctionUtils;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.spi.WorkflowEntry;
import org.ofbiz.core.entity.GenericEntityException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.loader.DescriptorFactory;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Date;

/**
 * A validator with no configuration to check that a date is not in the past.
 * Failure to validate throws an InvalidInputException.
 */
public class ValidatorNoConfig implements Validator
{
    private static final Logger log = Logger.getLogger(ValidatorNoConfig.class);

    public void validate(Map transientVars, 
                         Map args, 
                         PropertySet ps) throws InvalidInputException {
        try {
            // Get the issue as modified in the transition screen
            Issue issue = (Issue) transientVars.get("issue");

            // Get the date value from a hard-coded field name
            String fieldName = "Future Date Field";
            CustomField cf = ManagerFactory.getCustomFieldManager().getCustomFieldObjectByName(fieldName);
            if (cf == null) {
                throw new InvalidInputException("The validator failed to validate because it could not find the date custom field: " + fieldName);
            }

            // This will also use the default value, if any
            Date value = (Date) issue.getCustomFieldValue(cf);
            if (value == null) {
                // The field was not set or was not present in the
                // transition screen
                return;
            }
            log.debug("The updated issue has a custom field value of : " + value);

            // Check that the date is in not in the past. Today is not
            // valid either.
            Date today = new Date();
            if (value.compareTo(today) < 0) {
                // It would be nice to show the date in the local format
                throw new InvalidInputException("The validator failed to validate because the date " + value + " is in the past");
            }
        } catch (DataAccessException e) {
            throw new InvalidInputException("The validator failed to validate because it could not find the issue");
        }
    }

}
