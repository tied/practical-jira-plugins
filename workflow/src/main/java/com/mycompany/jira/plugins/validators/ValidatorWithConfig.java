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
 * A validator with configuration to check that a date is not more than 
 * a given number of days in the past.
 */
public class ValidatorWithConfig implements Validator
{
    private static final Logger log = Logger.getLogger(ValidatorWithConfig.class);

    public void validate(Map transientVars, 
                         Map args, 
                         PropertySet ps) throws InvalidInputException {
        String configparam = configparam = (String) args.get("mycurrentvalue");
        try {
            // Get the issue as modified in the transition screen
            Issue issue = (Issue) transientVars.get("issue");

            // Get the date value from a hard-coded field name
            String fieldName = "Future Date Field";
            CustomField cf = ManagerFactory.getCustomFieldManager().getCustomFieldObjectByName(fieldName);
            if (cf == null) {
                throw new InvalidInputException("The DateValidator failed to validate because it could not find the date custom field: " + fieldName);
            }

            // This will also use the default value, if any
            Date value = (Date) issue.getCustomFieldValue(cf);
            if (value == null) {
                return;
            }
            log.debug("The updated issue has a custom field value of : " + value);

            log.debug("This validator was configured with a value: " + configparam);
            // Check that the date is at least configparam days in the future
            Date today = new Date();
            // Convert from days to milliseconds
            long difference = Long.parseLong(configparam) * 24 * 3600 * 1000;
            if (value.getTime() < (today.getTime() + difference)) {
                // It would be nice to show the date in the local format
                throw new InvalidInputException("The DateValidator failed to validate because the date " + value + " is not at least " + configparam + " days in the future");
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidInputException("The DateValidator failed to validate the configured parameter " + configparam + " is not a number");
        } catch (DataAccessException e) {
            throw new InvalidInputException("The DateValidator failed to validate because it could not find the issue");
        }
    }

    /* 
     * There is no AbstractJiraValidator class but there should
     * be. The getIssue method below could be used to access the issue
     * before the changes in the transition screen.  
     */

    public static final String ORIGINAL_ISSUE_KEY = "originalissueobject";

    protected Issue getIssue(Map transientVars) throws DataAccessException {
        Issue issue = (Issue) transientVars.get(ORIGINAL_ISSUE_KEY);
        if (issue == null) {
            WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
            try {
                issue = ManagerFactory.getIssueManager().getIssueObjectByWorkflow(new Long(entry.getId()));
            } catch (GenericEntityException e) {
                throw new DataAccessException("Problem looking up issue with workflow entry id "+entry.getId());
            }
            if (issue == null) throw new DataAccessException("No issue found with workflow entry id "+entry.getId());
        }
        return issue;
    }

}
