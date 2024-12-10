package application;

import java.time.LocalTime;

public class Schedule {
	private int scheduleID;
	private int sportID;
	private String sportName;
	private int coachID;
	private String coachName;
	private LocalTime startTime;
	private LocalTime endTime;
	private String dayOfWeek;
	private String salle;
	private String note;
	
	public Schedule() {
		
	}

	public int getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public int getSportID() {
		return sportID;
	}

	public void setSportID(int sportID) {
		this.sportID = sportID;
	}

	public String getSportName() {
		return sportName;
	}

	public void setSportName(String sportName) {
		this.sportName = sportName;
	}

	public int getCoachID() {
		return coachID;
	}

	public void setCoachID(int coachID) {
		this.coachID = coachID;
	}

	public String getCoachName() {
		return coachName;
	}

	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getSalle() {
		return salle;
	}

	public void setSalle(String salle) {
		this.salle = salle;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	@Override
    public String toString() {
        return "Schedule{" +
                "scheduleID=" + scheduleID +
                ", sportID=" + sportID +
                ", sportName='" + sportName + '\'' +
                ", coachID=" + coachID +
                ", coachName='" + coachName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", salle='" + salle + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
	
}
