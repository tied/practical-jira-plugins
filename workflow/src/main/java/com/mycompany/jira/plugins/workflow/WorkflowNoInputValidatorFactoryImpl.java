package com.mycompany.jira.plugins.workflow;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class WorkflowNoInputValidatorFactoryImpl implements WorkflowPluginValidatorFactory {

    // This controls what appears in the Velocity context for viewing
    // the validator
    public Map getVelocityParams(String resourceName, 
                                 AbstractDescriptor descriptor) {
        // This is empty because we don't need any variables in the
        // Velocity context if we are not configuring the validator
        // yet.
        return new HashMap();
    }

    // This controls what is passed into the args parameter of the
    // validate method of the DateValidator class
    public Map getDescriptorParams(Map conditionParams) {
        return Collections.EMPTY_MAP;
    }
}
