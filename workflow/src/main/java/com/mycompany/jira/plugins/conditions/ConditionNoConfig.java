package com.mycompany.jira.plugins.conditions;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.user.User;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import java.util.Map;
import org.apache.log4j.Logger;

public class ConditionNoConfig extends AbstractJiraCondition {
    private static final Logger log = Logger.getLogger(ConditionNoConfig.class);

    /**
     * @param transientVars a Map of key/value pairs populated by the
        Factory class for this condition
     * @param args and ps used for configurable conditions
     *
     * @return true if the condition passes for the current user
     */
    public boolean passesCondition(Map transientVars,
                                   Map args, 
                                   PropertySet ps) throws WorkflowException {
        log.debug("Checking the noconfig condition");
        
        // This is the user who is viewing the issue that has this transition
        User user = getCaller(transientVars, args);
        
        try {
            Issue issue = getIssue(transientVars);
            // More logic would usually go here
            return true;
        } catch (DataAccessException e) {
            log.warn("Failed to find the expected issue", e);
        }
        return false;
    }

}
