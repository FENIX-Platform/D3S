package org.fao.fenix.d3s.msd.dto.common;

import org.fao.fenix.d3s.msd.dto.dsd.DSDDimension;

import java.util.HashMap;
import java.util.Map;

public class ValueOperator {
	
	private String implementation;
	private String rule;
	private Map<String,Object> fixedParameters;
    DSDDimension dimension;


    public ValueOperator() {}
    public ValueOperator(String implementation, String rule, String dimensionName) {
        this.implementation = implementation;
        this.rule = rule;
        this.dimension = dimensionName!=null ? new DSDDimension(dimensionName) : null;
    }

	//GET-SET
	public String getImplementation() {
		return implementation;
	}
	public void setImplementation(String implementation) {
		this.implementation = implementation;
        keyUpdated = true;
	}
	public Map<String, Object> getFixedParameters() {
		return fixedParameters;
	}
	public void setFixedParameters(Map<String, Object> fixedParameters) {
		this.fixedParameters = fixedParameters!=null ? new HashMap<String, Object>(fixedParameters) : null;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
        keyUpdated = true;
	}
    public DSDDimension getDimension() {
        return dimension;
    }
    public void setDimension(DSDDimension dimension) {
        this.dimension = dimension;
    }

    //Utils
	public void addFixedParameter(String key, Object value) {
        if (fixedParameters==null)
            fixedParameters = new HashMap<String, Object>();
		fixedParameters.put(key, value);
        keyComponents.append(key.hashCode() + value.hashCode());
        keyUpdated = true;
	}


    //Compare
    private boolean keyUpdated;
    private StringBuilder keyComponents = new StringBuilder();
    private String key;
    public String getKey() {
        if (keyUpdated) {
            key = new StringBuilder()
            .append(implementation != null ? implementation.hashCode() : 0).append('|')
            .append(rule != null ? rule.hashCode() : 0).append('|')
            .append(keyComponents.toString()).toString();
            keyUpdated = false;
        }
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        return obj!=null && obj instanceof ValueOperator && ((ValueOperator)obj).getKey().equals(getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
