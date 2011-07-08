package com.mycompany.jira.plugins.workflow;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.core.util.map.EasyMap;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class WorkflowWithInputValidatorFactoryImpl 
    extends AbstractWorkflowPluginFactory 
    implements WorkflowPluginValidatorFactory {

    protected void getVelocityParamsForView(Map velocityParams, 
                                            AbstractDescriptor descriptor) {
        if (!(descriptor instanceof ValidatorDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a ValidatorDescriptor.");
        }

        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
        String value = (String) validatorDescriptor.getArgs().get("mycurrentvalue");
        if (value == null) {
            value = "Not yet set";
        }
        velocityParams.put("mycurrentvalue", value);
    }

    protected void getVelocityParamsForInput(Map velocityParams) {
        // Text fields don't need any other parameters but 
        // let's add one for the example
        velocityParams.put("myinputvalue", "example");
    }

    protected void getVelocityParamsForEdit(Map velocityParams, 
                                            AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    // This controls what is passed into the validate method of the
    // MyCustomCondition class
    public Map getDescriptorParams(Map validatorParams) {
        // Read the one parameter we care about from the input velocity template
        String value = extractSingleParam(validatorParams, "mycurrentvalue");

        // This key is what MyCustomCondition will look for in the args variable
        return EasyMap.build("mycurrentvalue", value);
    }
}
