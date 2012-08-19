package com.mycompany.jira.plugins.workflow;

import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.core.util.map.EasyMap;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class WorkflowWithInputPostfunctionFactoryImpl 
    extends AbstractWorkflowPluginFactory 
    implements WorkflowPluginFunctionFactory {

    protected void getVelocityParamsForView(Map velocityParams, 
                                            AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }

        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
        String value = (String) functionDescriptor.getArgs().get("mycurrentvalue");
        if (value == null) {
            value = "Not yet set";
        }
        velocityParams.put("mycurrentvalue", value);
    }

    protected void getVelocityParamsForInput(Map velocityParams) {
        // We could set a default value here
    }

    protected void getVelocityParamsForEdit(Map velocityParams, 
                                            AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    // This controls what is passed into the execute method of the
    // FunctionWithConfig class
    public Map getDescriptorParams(Map functionParams) {
        // Read the one parameter we care about from the input velocity template
        String value = extractSingleParam(functionParams, "mycurrentvalue");

        // This key is what FunctionWithConfig will look for in the
        // args variable
        return EasyMap.build("mycurrentvalue", value);
    }
}
