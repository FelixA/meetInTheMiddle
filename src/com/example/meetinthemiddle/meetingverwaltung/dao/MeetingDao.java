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
			Long verkehrsmittel_pers1_fk, String kommentar, Long verkehrsmittel_pers2_fk, String aIdSender, String aIdEmpfaenger, String message, String locationPers1, String locationPers2) throws ParseException {
		Meeting meeting = new Meeting(pers1_fk, pers2_fk, uhrzeit, lokalitaet_fk, ort_fk, bewertung, verkehrsmittel_pers1_fk, kommentar, verkehrsmittel_pers2_fk,aIdSender,aIdEmpfaenger, message, locationPers1, locationPers2);
		WebServiceClient.post(meeting, "/rest/meetings", context, Constants.DATE_FORMAT_JAXB);
	}
	public void update(Long id, int bewertung, String kommentar, Long verkehrsmittel_pers2_fk, String locationPers2){
		Meeting meeting = new Meeting(id,bewertung, kommentar, verkehrsmittel_pers2_fk, locationPers2);
		WebServiceClient.put(meeting, "/rest/meetings", context, Constants.DATE_FORMAT_JAXB);

	}
	public List<Meeting> findMeetingByPers1_fk(Long id) {
		MeetingList meeting = WebServiceClient.get(MeetingList.class, url + "pers1_fk/"
				+ id, context, Constants.DATE_FORMAT_JAXB);
		return meeting.getMeetings();
	}
	
	public List<Meeting> findMeetingByPers2_fk(Long id) {
		MeetingList meeting = WebServiceClient.get(MeetingList.class, url + "pers2_fk/"
				+ id, context, Constants.DATE_FORMAT_JAXB);
		return meeting.getMeetings();
	}

	public Meeting findMeetingByPers2_FK_Uhrzeit(String id, String hour,
			String minute) {
		Meeting meeting = WebServiceClient.get(Meeting.class, url
				+ "pers2_fk/" +  id +"/" + hour + "/" + minute, context, Constants.DATE_FORMAT_JAXB);
		return meeting;
	}
	
}