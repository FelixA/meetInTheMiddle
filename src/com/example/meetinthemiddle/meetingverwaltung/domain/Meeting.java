package com.example.meetinthemiddle.meetingverwaltung.domain;

import static com.example.meetinthemiddle.util.Constants.NAMESPACE_MEETINGVERWALTUNG;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(reference=NAMESPACE_MEETINGVERWALTUNG)
public class Meeting {

	public Meeting(Long pers1_fk, Long pers2_fk, Date uhrzeit,
			Long lokalitaet_fk, Long ort_fk, Integer bewertung,
			Long verkehrsmittel_pers1_fk, String kommentar,
			Long verkehrsmittel_pers2_fk) {
		super();
		this.pers1_fk = pers1_fk;
		this.pers2_fk = pers2_fk;
		this.uhrzeit = uhrzeit;
		this.lokalitaet_fk = lokalitaet_fk;
		this.ort_fk = ort_fk;
		this.bewertung = bewertung;
		this.verkehrsmittel_pers1_fk = verkehrsmittel_pers1_fk;
		this.kommentar = kommentar;
		this.verkehrsmittel_pers2_fk = verkehrsmittel_pers2_fk;
	}
	public Meeting() {}
	@Element
	private Long pers1_fk;
	@Element
	private Long pers2_fk;
	@Element
	private Date uhrzeit;
	@Element(required = false)
	private Long lokalitaet_fk;
	@Element(required = false)
	private Long ort_fk;
	@Element(required = false)
	private Integer bewertung;
	@Element(required = false)
	private Long verkehrsmittel_pers1_fk;
	@Element(required = false)
	private String kommentar;
	@Element(required = false)
	private Long verkehrsmittel_pers2_fk;
	@Override
	public String toString() {
		return "Meeting [pers1_fk=" + pers1_fk + ", pers2_fk=" + pers2_fk
				+ ", uhrzeit=" + uhrzeit + ", lokalitaet_fk=" + lokalitaet_fk
				+ ", ort_fk=" + ort_fk + ", bewertung=" + bewertung
				+ ", verkehrsmittel_pers1_fk=" + verkehrsmittel_pers1_fk
				+ ", kommentar=" + kommentar + ", verkehrsmittel_pers2_fk="
				+ verkehrsmittel_pers2_fk + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bewertung;
		result = prime * result
				+ ((kommentar == null) ? 0 : kommentar.hashCode());
		result = prime * result
				+ ((lokalitaet_fk == null) ? 0 : lokalitaet_fk.hashCode());
		result = prime * result + ((ort_fk == null) ? 0 : ort_fk.hashCode());
		result = prime * result
				+ ((pers1_fk == null) ? 0 : pers1_fk.hashCode());
		result = prime * result
				+ ((pers2_fk == null) ? 0 : pers2_fk.hashCode());
		result = prime * result + ((uhrzeit == null) ? 0 : uhrzeit.hashCode());
		result = prime
				* result
				+ ((verkehrsmittel_pers1_fk == null) ? 0
						: verkehrsmittel_pers1_fk.hashCode());
		result = prime
				* result
				+ ((verkehrsmittel_pers2_fk == null) ? 0
						: verkehrsmittel_pers2_fk.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meeting other = (Meeting) obj;
		if (bewertung != other.bewertung)
			return false;
		if (kommentar == null) {
			if (other.kommentar != null)
				return false;
		} else if (!kommentar.equals(other.kommentar))
			return false;
		if (lokalitaet_fk == null) {
			if (other.lokalitaet_fk != null)
				return false;
		} else if (!lokalitaet_fk.equals(other.lokalitaet_fk))
			return false;
		if (ort_fk == null) {
			if (other.ort_fk != null)
				return false;
		} else if (!ort_fk.equals(other.ort_fk))
			return false;
		if (pers1_fk == null) {
			if (other.pers1_fk != null)
				return false;
		} else if (!pers1_fk.equals(other.pers1_fk))
			return false;
		if (pers2_fk == null) {
			if (other.pers2_fk != null)
				return false;
		} else if (!pers2_fk.equals(other.pers2_fk))
			return false;
		if (uhrzeit == null) {
			if (other.uhrzeit != null)
				return false;
		} else if (!uhrzeit.equals(other.uhrzeit))
			return false;
		if (verkehrsmittel_pers1_fk == null) {
			if (other.verkehrsmittel_pers1_fk != null)
				return false;
		} else if (!verkehrsmittel_pers1_fk
				.equals(other.verkehrsmittel_pers1_fk))
			return false;
		if (verkehrsmittel_pers2_fk == null) {
			if (other.verkehrsmittel_pers2_fk != null)
				return false;
		} else if (!verkehrsmittel_pers2_fk
				.equals(other.verkehrsmittel_pers2_fk))
			return false;
		return true;
	}
	public Long getPers1_fk() {
		return pers1_fk;
	}
	public void setPers1_fk(Long pers1_fk) {
		this.pers1_fk = pers1_fk;
	}
	public Long getPers2_fk() {
		return pers2_fk;
	}
	public void setPers2_fk(Long pers2_fk) {
		this.pers2_fk = pers2_fk;
	}
	public Date getUhrzeit() {
		return uhrzeit;
	}
	public void setUhrzeit(Date uhrzeit) {
		this.uhrzeit = uhrzeit;
	}
	public Long getLokalitaet_fk() {
		return lokalitaet_fk;
	}
	public void setLokalitaet_fk(Long lokalitaet_fk) {
		this.lokalitaet_fk = lokalitaet_fk;
	}
	public Long getOrt_fk() {
		return ort_fk;
	}
	public void setOrt_fk(Long ort_fk) {
		this.ort_fk = ort_fk;
	}
	public Integer getBewertung() {
		return bewertung;
	}
	public void setBewertung(Integer bewertung) {
		this.bewertung = bewertung;
	}
	public Long getVerkehrsmittel_pers1_fk() {
		return verkehrsmittel_pers1_fk;
	}
	public void setVerkehrsmittel_pers1_fk(Long verkehrsmittel_pers1_fk) {
		this.verkehrsmittel_pers1_fk = verkehrsmittel_pers1_fk;
	}
	public String getKommentar() {
		return kommentar;
	}
	public void setKommentar(String kommentar) {
		this.kommentar = kommentar;
	}
	public Long getVerkehrsmittel_pers2_fk() {
		return verkehrsmittel_pers2_fk;
	}
	public void setVerkehrsmittel_pers2_fk(Long verkehrsmittel_pers2_fk) {
		this.verkehrsmittel_pers2_fk = verkehrsmittel_pers2_fk;
	}

}
