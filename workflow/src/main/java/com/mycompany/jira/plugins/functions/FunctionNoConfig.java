package com.mycompany.jira.plugins.functions;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import org.apache.log4j.Logger;
import java.util.Map;
import java.util.Date;
import java.util.Iterator;

public class FunctionNoConfig extends AbstractJiraFunctionProvider {

    private static final Logger log = Logger.getLogger(FunctionNoConfig.class);

    public void execute(Map transientVars, 
                        Map args, 
                        PropertySet ps) throws WorkflowException {
        log.debug("The post-function is ready to do something");
        
        log.debug("transientVars contains:\n" + dumpMap(transientVars));

        MutableIssue issue = getIssue(transientVars);
        String description = issue.getDescription();
        if (description == null) {
            description = "";
        }
        if (description.length() > 512) {
            // This is displayed on a screen as a Workflow Error
            throw new WorkflowException("The description text is longer than 512 characters");
        }
        issue.setDescription(description + "\nPost-function called at " + new Date());
    }        

    private String dumpMap(Map transientVars) {
        StringBuffer sb = new StringBuffer();
        for (Iterator it = transientVars.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

}