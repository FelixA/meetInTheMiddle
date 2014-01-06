package com.example.meetinthemiddle.placesverwaltung.domain;

import static com.example.meetinthemiddle.util.Constants.NAMESPACE_PLACESVERWALTUNG;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
@Namespace(reference = NAMESPACE_PLACESVERWALTUNG)
public class Place {

	public Place(String stadtname, String plz) {
		super();
		this.stadtname = stadtname;
		this.plz = plz;
	}

	public Place() {

	}

	@Element(required = false)
	private long id;
	@Element
	private String stadtname;
	@Element
	private String plz;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStadtname() {
		return stadtname;
	}

	public void setStadtname(String stadtname) {
		this.stadtname = stadtname;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result
				+ ((stadtname == null) ? 0 : stadtname.hashCode());
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
		Place other = (Place) obj;
		if (id != other.id)
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} else if (!plz.equals(other.plz))
			return false;
		if (stadtname == null) {
			if (other.stadtname != null)
				return false;
		} else if (!stadtname.equals(other.stadtname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Location [id=" + id + ", stadtname=" + stadtname + ", plz="
				+ plz + "]";
	}
}
