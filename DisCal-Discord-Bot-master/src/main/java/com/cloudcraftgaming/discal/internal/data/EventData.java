package com.cloudcraftgaming.discal.internal.data;

/**
 * Created by Nova Fox on 6/1/17.
 * Website: www.cloudcraftgaming.com
 * For Project: DisCal
 */
public class EventData {
	private final long guildId;

	private String eventId;
	private long eventEnd;
	private String imageLink;

	public EventData(long _guildId) {
		guildId = _guildId;
	}

	//Getters
	public long getGuildId() {
		return guildId;
	}

	public String getEventId() {
		return eventId;
	}

	public long getEventEnd() {
		return eventEnd;
	}

	public String getImageLink() {
		return imageLink;
	}

	//Setters
	public void setEventId(String _eventId) {
		eventId = _eventId;
	}

	public void setEventEnd(long _eventEnd) {
		eventEnd = _eventEnd;
	}

	public void setImageLink(String _link) {
		imageLink = _link;
	}

	//Boolean/Checkers
	public boolean shouldBeSaved() {
		return imageLink != null;
	}
}