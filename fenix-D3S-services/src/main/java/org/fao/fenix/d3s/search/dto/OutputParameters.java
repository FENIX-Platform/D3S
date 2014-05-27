package org.fao.fenix.d3s.search.dto;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.fao.fenix.commons.msd.dto.full.cl.Code;
import org.fao.fenix.commons.msd.dto.full.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.full.common.ValueOperator;
import org.fao.fenix.commons.msd.dto.type.dsd.DSDDataType;
import org.fao.fenix.commons.utils.JSONUtils;

public class OutputParameters {
	
	private Boolean out = true;
	private Boolean key;
	private Code um;
	private Integer codesLevel;
	private ValueOperator aggregation;
	//private SearchFilter filter;
    private DSDDataType dataType;
    private CodeSystem codeSystem;


    public OutputParameters() {}
    public OutputParameters(Boolean out, Boolean key) {
        this.out = out;
        this.key = key;
    }


    //GET-SET
    public CodeSystem getCodeSystem() {
        return codeSystem;
    }
    public void setCodeSystem(CodeSystem codeSystem) {
        this.codeSystem = codeSystem;
    }
    public DSDDataType getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = DSDDataType.getByCode(dataType);
    }
    public Boolean getOut() {
		return out;
	}
	public void setOut(Boolean out) {
		this.out = out;
	}
	public Boolean getKey() {
		return key!=null ? key : Boolean.FALSE;
	}
	public void setKey(Boolean key) {
		this.key = key;
	}
	public Code getUm() {
		return um;
	}
	public void setUm(Code um) {
		this.um = um;
	}
	public Integer getCodesLevel() {
		return codesLevel;
	}
	public void setCodesLevel(Integer codesLevel) {
		this.codesLevel = codesLevel;
	}
	public ValueOperator getAggregation() {
		return aggregation;
	}
	public void setAggregation(ValueOperator aggregation) {
		this.aggregation = aggregation;
	}
/*	public SearchFilter getFilter() {
		return filter;
	}
	public void setFilter(SearchFilter filter) {
		this.filter = filter;
	}
*/

	//Conversion
	public static OutputParameters getInstance(Map<String,Object> raw) {
		try { return JSONUtils.toObject(JSONUtils.toJSON(raw), OutputParameters.class);
		} catch (Exception e) { return null; }
	}

    //Utils
    @JsonIgnore
    public String getIdKey() {
        return new StringBuilder()
                .append(out!=null ? out : '|')
                .append(key!=null ? key : '|')
                .append(um!=null ? um.getGlobalCode() : '|')
                .append(codeSystem!=null ? codeSystem.getSystem() : '|')
                .append(codeSystem!=null ? codeSystem.getVersion() : '|')
                .append(codesLevel!=null ? codesLevel : '|')
                .append(aggregation!=null ? aggregation.getKey() : '|')
                .append(dataType!=null ? dataType.name() : '|').toString();
    }

}
