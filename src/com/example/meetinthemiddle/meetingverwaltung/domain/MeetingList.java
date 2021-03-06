package com.example.meetinthemiddle.meetingverwaltung.domain;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.example.meetinthemiddle.meetingverwaltung.domain.Meeting;

@Root(name = "meetings")
public class MeetingList {

	@ElementList(inline = true, entry = "meeting", required=false)
	private List<Meeting> meetings;

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

}
