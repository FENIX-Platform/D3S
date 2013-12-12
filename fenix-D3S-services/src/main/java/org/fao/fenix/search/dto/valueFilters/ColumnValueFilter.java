package org.fao.fenix.search.dto.valueFilters;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.server.utils.DataUtils;

public class ColumnValueFilter {
	
	private ValueFilterType type = ValueFilterType.unknown;
	private String regExp;
    private Pattern regExpPattern;
	private String language;
	private String text;
	private Date fromDate;
	private Date toDate;
	private Long from;
	private Long to;
	private Code code;
	private String id;
    private String enumeration;
    private boolean monthly = false;
    private boolean dayly = false;


	public ColumnValueFilter() {}
	public ColumnValueFilter(Code code) { this.code = code; setType(); }
	public ColumnValueFilter(String text) { this.text = text; setType(); }
	public ColumnValueFilter(String language, String text) { this.language = language; this.text = text; setType(); }
	public ColumnValueFilter(Date fromDate, Date toDate) { this.fromDate = fromDate; this.toDate = toDate; setType(); }
	public ColumnValueFilter(Long from, Long to) { this.from = from; this.to = to; setType(); }
	
	public ValueFilterType getType() {
		return type;
	}
    public void setType(ValueFilterType type) {
        this.type = type;
    }
    public boolean isDayly() {
        return dayly;
    }
    public void setDayly(boolean dayly) {
        this.dayly = dayly;
    }
    public boolean isMonthly() {
        return monthly;
    }
    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }
    public String getEnumeration() {
		return enumeration;
	}
	public void setEnumeration(String enumeration) {
		this.enumeration = enumeration;
		setType();
	}
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		setType();
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
		setType();
	}
	public String getRegExp() {
		return regExp;
	}
	public void setRegExp(String regExp) {
		this.regExp = regExp;
        setRegExpPattern();
		setType();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		setType();
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromYear(Integer fromYear) {
		Calendar c = Calendar.getInstance();
		c.set(fromYear, 0, 1);
        setFromDate(c.getTime());
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = DataUtils.roundDate(fromDate);
        if (toDate==null)
            toDate = fromDate;
		setType();
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToYear(Integer toYear) {
		Calendar c = Calendar.getInstance();
		c.set(toYear, 11, 31);
		setToDate(c.getTime());
	}
	public void setToDate(Date toDate) {
		this.toDate = DataUtils.roundDate(toDate);
        if (fromDate==null)
            fromDate = toDate;
		setType();
	}
	public Long getFrom() {
		return from;
	}
	public void setFrom(Long from) {
		this.from = from;
        if (to == null)
            to = from;
		setType();
	}
	public Long getTo() {
		return to;
	}
	public void setTo(Long to) {
		this.to = to;
        if (from==null)
            from = to;
		setType();
	}
	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code = code;
		setType();
	}
    @JsonIgnore
    public Pattern getRegExpPatternLike() {
        return regExpPattern;
    }
    @JsonIgnore
    public void setRegExpPattern(Pattern regExpPattern) {
        this.regExpPattern = regExpPattern;
    }
    @JsonIgnore
    Collection<Long> getTimeSeries(DSDDataType toDataType) throws Exception {
        return type==ValueFilterType.dateInterval ? DataUtils.getTimeSeries(fromDate,toDate,toDataType) : null;
    }

	//Utils
    private void setRegExpPattern() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('^');
        for (char c : (regExp==null || regExp.trim().length()==0 ? new char[]{'*'} : regExp.toCharArray()))
            if (c=='*')
                buffer.append('.').append('*');
            else if (c=='[' || c==']' || c=='$' || c=='^' || c=='-' || c=='|' || c=='\\' || c=='+' || c=='?' || c=='{' || c=='}' || c=='(' || c==')')
                buffer.append('\\').append(c);
            else
                buffer.append(c);
        buffer.append('$');

        regExpPattern = Pattern.compile(buffer.toString());
    }


	private void setType() {
		if (language!=null && text!=null)
			type=ValueFilterType.iText;
		else if (language!=null && regExp!=null)
			type=ValueFilterType.iLike;
		else if (text!=null)
			type=ValueFilterType.text;
		else if (regExp!=null)
			type=ValueFilterType.like;
		else if (from!=null && to!=null)
			type=ValueFilterType.numberInterval;
		else if (fromDate!=null && toDate!=null)
			type=ValueFilterType.dateInterval;
		else if (code!=null)
			type=ValueFilterType.code;
		else if (id!=null)
			type=ValueFilterType.document;
		else if (enumeration!=null)
			type=ValueFilterType.enumeration;
		else
			type=ValueFilterType.unknown;
	}

    @JsonIgnore
    public String getKey() {
        if (type==null)
            setType();
        if (type!=null)
            switch (type) {
                case customCode: return type+code.getGlobalCode();
                case code: return type+code.getGlobalCode();
                case dateInterval: return type.name()+fromDate.getTime()+toDate.getTime();
                case document: return type+id;
                case enumeration: return type+enumeration;
                case iLike: return type+language+regExp;
                case iText: return type+language+text;
                case like: return type+regExp;
                case numberInterval: return type.name()+from+to;
                case text: return type+text;
            }
        return null;
    }

}
