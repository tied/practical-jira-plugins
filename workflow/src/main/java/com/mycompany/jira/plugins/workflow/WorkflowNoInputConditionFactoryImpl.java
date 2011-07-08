package com.mycompany.jira.plugins.workflow;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class WorkflowNoInputConditionFactoryImpl implements WorkflowPluginConditionFactory {

    // This controls what appears in the Velocity context for viewing
    // the condition
    public Map getVelocityParams(String resourceName, 
                                 AbstractDescriptor descriptor) {
        // This is empty because we don't need any variables in the
        // Velocity context if we are not configuring the condition
        // yet.
        return new HashMap();
    }

    // This controls what is passed in the args parameter of the 
    // passesCondition method of the ConditionNoConfig class
    public Map getDescriptorParams(Map conditionParams) {
        // This is empty because we don't need any variables in the
        // Velocity context as we are not configuring the condition
        return Collections.EMPTY_MAP;
    }
}
