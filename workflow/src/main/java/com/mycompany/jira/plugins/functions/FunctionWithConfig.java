package com.mycompany.jira.plugins.functions;

import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import org.apache.log4j.Logger;
import java.util.Map;

public class FunctionWithConfig extends AbstractJiraFunctionProvider {

    private static final Logger log = Logger.getLogger(FunctionWithConfig.class);

    public void execute(Map transientVars, 
                        Map args, 
                        PropertySet ps) throws WorkflowException {
        // The issue is modified by changing the values in the transientVars
        // which is processed later on after the post-function has finished.
        log.debug("The configurable post-function is ready to do something");
        
        String configparam = (String) args.get("mycurrentvalue");
        log.debug("This post-function was configured with a value: " + configparam);

        if (false) {
            // This is displayed on a screen as a Workflow Error
            throw new WorkflowException("Something went wrong");
        }
    }

}