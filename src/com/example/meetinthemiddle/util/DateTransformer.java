package com.example.meetinthemiddle.util;

import java.text.DateFormat;
import java.util.Date;

import org.simpleframework.xml.transform.Transform;

public class DateTransformer implements Transform<Date> {
	private DateFormat dateFormat;

	public DateTransformer(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public Date read(String value) throws Exception {
		return dateFormat.parse(value);
	}

	@Override
	public String write(Date value) throws Exception {
		return dateFormat.format(value);
	}
}
