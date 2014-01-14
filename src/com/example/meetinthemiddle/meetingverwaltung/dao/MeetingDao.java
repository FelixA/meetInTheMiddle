package com.example.meetinthemiddle.meetingverwaltung.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;
import com.example.meetinthemiddle.meetingverwaltung.domain.MeetingList;
import com.example.meetinthemiddle.util.Constants;
import com.example.meetinthemiddle.util.WebServiceClient;

public class MeetingDao {
	private Context context;
	private String url = "/rest/meetings/";

	public MeetingDao(Context ctx) {
		this.context = ctx;
	}

	public List<Meeting> selectAll() {
		MeetingList list = WebServiceClient.get(MeetingList.class,
				url, context, Constants.DATE_FORMAT_JAXB);

		return list.getMeetings();
	}

	public Meeting findMeetingById(Long id) {
		Meeting meeting = WebServiceClient.get(Meeting.class, url
				+ id, context, Constants.DATE_FORMAT_JAXB);
		return meeting;
	}

	public void create(Long pers1_fk, Long pers2_fk, Date uhrzeit,
			Long lokalitaet_fk, Long ort_fk, Integer bewertung,
			Long verkehrsmittel_pers1_fk, String kommentar, Long verkehrsmittel_pers2_fk) throws ParseException {
  
		Meeting meeting = new Meeting(pers1_fk, pers2_fk, uhrzeit, lokalitaet_fk, ort_fk, bewertung, verkehrsmittel_pers1_fk, kommentar, verkehrsmittel_pers2_fk);
		WebServiceClient.post(meeting, "/rest/meetings", context, Constants.DATE_FORMAT_JAXB);
	}
	public void update(Long pers1_fk, Long pers2_fk, Date uhrzeit,
			Long lokalitaet_fk, Long ort_fk, int bewertung,
			Long verkehrsmittel_pers1_fk, String kommentar, Long verkehrsmittel_pers2_fk){
		Meeting meeting = new Meeting(pers1_fk, pers2_fk, uhrzeit, lokalitaet_fk, ort_fk, bewertung, verkehrsmittel_pers1_fk, kommentar, verkehrsmittel_pers2_fk);
		WebServiceClient.put(meeting, "/rest/meetings", context, Constants.DATE_FORMAT_JAXB);

	}
}
