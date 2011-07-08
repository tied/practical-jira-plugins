package com.mycompany.jira.plugins.workflow;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.core.util.map.EasyMap;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class WorkflowWithInputConditionFactoryImpl 
    extends AbstractWorkflowPluginFactory 
    implements WorkflowPluginConditionFactory {

    protected void getVelocityParamsForView(Map velocityParams, 
                                            AbstractDescriptor descriptor) {
        if (!(descriptor instanceof ConditionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        }

        ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
        String value = (String) conditionDescriptor.getArgs().get("mycurrentvalue");
        if (value == null) {
            value = "Not yet set";
        }
        velocityParams.put("mycurrentvalue", value);
    }

    protected void getVelocityParamsForInput(Map velocityParams) {
        // Text fields don't need any other parameters
        // velocityParams.put("myinputvalue", "example");
    }

    protected void getVelocityParamsForEdit(Map velocityParams, 
                                            AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    /**
     * @param A set of parameters from the web page.
     * @return a Map of values that is passed into the passesCondition
     * method and is also available via descriptor.getArgs()
     */
    public Map getDescriptorParams(Map conditionParams) {
        // Read the one parameter we care about from the input velocity template
        String value = extractSingleParam(conditionParams, "mycurrentvalue");

        // passesCondition will look for this key in the args variable
        return EasyMap.build("mycurrentvalue", value);
    }
}
