package org.fao.fenix.d3s.msd.dao.cl;

import java.util.*;

import org.fao.fenix.commons.msd.dto.cl.type.DuplicateCodeException;
import org.fao.fenix.d3s.msd.dao.common.CommonsConverter;
import org.fao.fenix.d3s.msd.dao.dsd.DSDConverter;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.cl.CodeRelationship;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.cl.type.CSSharingPolicy;
import org.fao.fenix.commons.msd.dto.cl.type.CodeRelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class CodeListConverter {
	
	public static final int ALL_LEVELS = -1;
	public static final int NO_LEVELS = 0;

	@Autowired private CommonsConverter cmConverter;
	@Autowired private DSDConverter dsdConverter;

    /****************************************************************************************/
	//CODE LIST CONVERSION
	public Collection<CodeSystem> toSystem (Collection<ODocument> systemsO) {
		if (systemsO==null)
			return null;
		Collection<CodeSystem> systems = new LinkedList<>();
		for (ODocument systemO : systemsO)
			systems.add(toSystem(systemO, false));
		return systems;
	}
	
	@SuppressWarnings("unchecked")
	public CodeSystem toSystem (ODocument systemO, boolean all) {
		CodeSystem system = new CodeSystem();
		system.setSystem((String)systemO.field("system"));
		system.setVersion((String)systemO.field("version"));
		system.setTitle((Map<String,String>)systemO.field("title",Map.class));
		system.setDescription((Map<String,String>)systemO.field("abstract",Map.class));
		system.setStartDate((Date)systemO.field("startDate"));
		system.setEndDate((Date)systemO.field("endDate"));
		system.setVirtualDate((Date)systemO.field("virtualDate"));
		system.setSharingPolicy(CSSharingPolicy.getByCode((String)systemO.field("sharingPolicy")));
		system.setLevelsNumber((Integer)systemO.field("levelsNumber"));

		//connected elements
		Collection<ODocument> keywords = (Collection<ODocument>)systemO.field("keywords");
		if (keywords!=null)
			for (ODocument keywordO : keywords)
				system.addKeyWord((String)keywordO.field("keyword"));

		system.setCategory(toCode((ODocument)systemO.field("category"), false, NO_LEVELS, null));
		system.setRegion(toCode((ODocument)systemO.field("region"), false, NO_LEVELS, null));
		system.setSource(cmConverter.toContactIdentity((ODocument)systemO.field("source")));
		system.setProvider(cmConverter.toContactIdentity((ODocument)systemO.field("provider")));
		
		//code list
		if (all)
            try {
                Collection<ODocument> rootCodes = systemO.field("rootCodes");
                Map<String, Code> codesBuffer = new HashMap<>();
                if (rootCodes!=null)
                    for (ODocument codeO : rootCodes)
                        system.addCode(toCode(codeO, system, ALL_LEVELS, codesBuffer));
            } catch (DuplicateCodeException e) {
                throw new RuntimeException("Malformed code list data into database", e);
            }

		return system;
	}



    /****************************************************************************************/
	//CODE CONVERSION
	public Collection<Code> toCode(Collection<ODocument> codesO, boolean codeSystem) {
		Collection<Code> codes = new LinkedList<Code>();
		for (ODocument codeO : codesO)
			codes.add(toCode(codeO, codeSystem, NO_LEVELS, null));
		return codes;
	}	
	public Code toCode(ODocument codeO, boolean codeSystem, int levels, Map<String,Code> done) {
        return codeO!=null  ? toCode(codeO, codeSystem ? toSystem((ODocument)codeO.field("system"), false) : null, levels, done) : null;
    }
	@SuppressWarnings("unchecked")
	private Code toCode(ODocument codeO, CodeSystem codeSystem, int levels, Map<String,Code> done) {
		if (codeO==null)
			return null;

        Code code = done!=null ? done.get(codeO.field("code")) : null;
        if (code==null) {
            code = new Code();
            code.setCode((String) codeO.field("code"));
            code.setLevel((Integer) codeO.field("level"));
            code.setTitle((Map<String, String>) codeO.field("title", Map.class));
            code.setDescription((Map<String, String>) codeO.field("abstract", Map.class));
            code.setSupplemental((Map<String, String>) codeO.field("supplemental", Map.class));
            code.setFromDate((Date) codeO.field("fromDate"));
            code.setToDate((Date) codeO.field("toDate"));
            //Set system
            if (codeSystem!=null)
                code.setSystem(codeSystem);
            else {
                code.setSystemKey((String) codeO.field("system.system"));
                code.setSystemVersion((String) codeO.field("system.version"));
            }
            //Aggregation rules
            Collection<ODocument> aggregationRules = codeO.field("aggregationRules");
            if (aggregationRules != null)
                for (ODocument aggregationRule : aggregationRules)
                    code.addAggregationRule(cmConverter.toOperator(aggregationRule));

            //Set hierarchy
            try {
                if (done!=null) {
                    done.put(code.getCode(), code); //IMPORTANT
                    if (levels!=NO_LEVELS && codeO.field("parents")!=null)
                        for (ODocument parentO : (Collection<ODocument>)codeO.field("parents"))
                            code.addParent(toCode(parentO, codeSystem, levels == ALL_LEVELS ? ALL_LEVELS : levels - 1, done));
                }
                if (levels!=NO_LEVELS && codeO.field("childs")!=null)
                    for (ODocument childO : (Collection<ODocument>)codeO.field("childs"))
                        code.addChild(toCode(childO, codeSystem, levels == ALL_LEVELS ? ALL_LEVELS : levels - 1, done));
            } catch (DuplicateCodeException e) {
                throw new RuntimeException("Malformed code list data into database", e);
            }
        }

		return code;
	}



    /****************************************************************************************/
	//RELATIONS CONVERSION
	public CodeRelationship toRelationship (ODocument relationO, OGraphDatabase database) {
		CodeRelationship relation = new CodeRelationship();
		relation.setType(CodeRelationshipType.getByCode((String)relationO.field("type")));
		relation.setFromCode(toCode((ODocument)relationO.field("out"), false, NO_LEVELS, null));
		relation.setToCode(toCode((ODocument)relationO.field("in"), false, NO_LEVELS, null));
		return relation;
	}
	public CodeConversion toConversion (ODocument conversionO, OGraphDatabase database) {
		CodeConversion conversion = new CodeConversion();
		conversion.setConversionRule(cmConverter.toOperator((ODocument)conversionO.field("conversionRule")));
		conversion.setFromCode(toCode((ODocument)conversionO.field("out"), true, NO_LEVELS, null));
		conversion.setToCode(toCode((ODocument)conversionO.field("in"), true, NO_LEVELS, null));
		return conversion;
	}
	public CodePropaedeutic toPropaedeutic (ODocument propaedeuticO, OGraphDatabase database) {
		CodePropaedeutic propaedeutic = new CodePropaedeutic();
		propaedeutic.setContextSystem(dsdConverter.toContext((ODocument)propaedeuticO.field("contextSystem")));
		propaedeutic.setFromCode(toCode((ODocument)propaedeuticO.field("out"), true, NO_LEVELS, null));
		propaedeutic.setToCode(toCode((ODocument)propaedeuticO.field("in"), true, NO_LEVELS, null));
		return propaedeutic;
	}


}