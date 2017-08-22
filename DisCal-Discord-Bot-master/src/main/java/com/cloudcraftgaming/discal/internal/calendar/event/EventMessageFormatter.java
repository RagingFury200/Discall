package com.cloudcraftgaming.discal.internal.calendar.event;

import com.cloudcraftgaming.discal.Main;
import com.cloudcraftgaming.discal.database.DatabaseManager;
import com.cloudcraftgaming.discal.internal.calendar.CalendarAuth;
import com.cloudcraftgaming.discal.internal.data.CalendarData;
import com.cloudcraftgaming.discal.internal.data.EventData;
import com.cloudcraftgaming.discal.internal.data.GuildSettings;
import com.cloudcraftgaming.discal.utils.EventColor;
import com.cloudcraftgaming.discal.utils.ExceptionHandler;
import com.cloudcraftgaming.discal.utils.ImageUtils;
import com.cloudcraftgaming.discal.utils.MessageManager;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by Nova Fox on 1/3/2017.
 * Website: www.cloudcraftgaming.com
 * For Project: DisCal
 */
@SuppressWarnings("Duplicates")
public class EventMessageFormatter {

    /**
     * Gets an EmbedObject for the specified event.
     * @param event The event involved.
     * @param settings The guild's settings
     * @return The EmbedObject of the event.
     */
    public static EmbedObject getEventEmbed(Event event, GuildSettings settings) {
		EventData ed = DatabaseManager.getManager().getEventData(settings.getGuildID(), event.getId());
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Info.Title", settings));
        if (ed.getImageLink() != null && ImageUtils.validate(ed.getImageLink())) {
			em.withImage(ed.getImageLink());
		}
        if (event.getSummary() != null) {
			String summary = event.getSummary();
			if (summary.length() > 250) {
				summary = summary.substring(0, 250);
				summary = summary + " (continues on Google Calendar View)";
			}

            em.appendField(MessageManager.getMessage("Embed.Event.Info.Summary", settings), summary, true);
        }
        if (event.getDescription() != null) {
        	String description = event.getDescription();
        	if (description.length() > 500) {
        		description = description.substring(0, 500);
        		description = description + " (continues on Google Calendar View)";
			}
            em.appendField(MessageManager.getMessage("Embed.Event.Info.Description", settings), description, true);
        }
        em.appendField(MessageManager.getMessage("Embed.Event.Info.StartDate", settings), getHumanReadableDate(event.getStart(), settings, false), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Info.StartTime", settings), getHumanReadableTime(event.getStart(), settings, false), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Info.EndDate", settings), getHumanReadableDate(event.getEnd(), settings, false), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Info.EndTime", settings), getHumanReadableTime(event.getEnd(), settings, false), true);

        try {
            //TODO: add support for multiple calendars...
            CalendarData data = DatabaseManager.getManager().getMainCalendar(settings.getGuildID());
            Calendar service;
			service = settings.useExternalCalendar() ? CalendarAuth.getCalendarService(settings) : CalendarAuth.getCalendarService();
            String tz = service.calendars().get(data.getCalendarAddress()).execute().getTimeZone();
            em.appendField(MessageManager.getMessage("Embed.Event.Info.TimeZone", settings), tz, true);
        } catch (Exception e1) {
            em.appendField(MessageManager.getMessage("Embed.Event.Info.TimeZone", settings), "Error/Unknown", true);
        }
        //TODO: Add info on recurrence here.
        em.withUrl(event.getHtmlLink());
        em.withFooterText(MessageManager.getMessage("Embed.Event.Info.ID", "%id%", event.getId(), settings));
        try {
            EventColor ec = EventColor.fromId(Integer.valueOf(event.getColorId()));
            em.withColor(ec.getR(), ec.getG(), ec.getB());
        } catch (Exception e) {
            //Color is null, ignore and add our default.
            em.withColor(56, 138, 237);
        }

        return em.build();
    }

    /**
     * Gets an EmbedObject for the specified event.
     * @param event The event involved.
     * @return The EmbedObject of the event.
     */
    public static EmbedObject getCondensedEventEmbed(Event event, GuildSettings settings) {
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Condensed.Title", settings));
        EventData ed = DatabaseManager.getManager().getEventData(settings.getGuildID(), event.getId());
        if (ed.getImageLink() != null && ImageUtils.validate(ed.getImageLink())) {
        	em.withThumbnail(ed.getImageLink());
		}
        if (event.getSummary() != null) {
        	String summary = event.getSummary();
        	if (summary.length() > 250) {
        		summary = summary.substring(0, 250);
        		summary = summary + " (continues on Google Calendar View)";
			}
            em.appendField(MessageManager.getMessage("Embed.Event.Condensed.Summary", settings), summary, true);
        }
        em.appendField(MessageManager.getMessage("Embed.Event.Condensed.Date", settings), getHumanReadableDate(event.getStart(), settings, false), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Condensed.ID", settings), event.getId(), false);
        em.withUrl(event.getHtmlLink());
        try {
            EventColor ec = EventColor.fromId(Integer.valueOf(event.getColorId()));
            em.withColor(ec.getR(), ec.getG(), ec.getB());
        } catch (Exception e) {
            //Color is null, ignore and add our default.
            em.withColor(56, 138, 237);
        }

        return em.build();
    }

    /**
     * Gets an EmbedObject for the specified PreEvent.
     * @param event The PreEvent to get an embed for.
     * @return The EmbedObject of the PreEvent.
     */
    public static EmbedObject getPreEventEmbed(PreEvent event, GuildSettings settings) {
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Pre.Title", settings));
        if (event.getEventData().getImageLink() != null && ImageUtils.validate(event.getEventData().getImageLink())) {
        	em.withImage(event.getEventData().getImageLink());
		}
        if (event.isEditing()) {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Id", settings), event.getEventId(), false);
        }
        if (event.getSummary() != null) {
			String summary = event.getSummary();
			if (summary.length() > 250) {
				summary = summary.substring(0, 250);
				summary = summary + " (continues on Google Calendar View)";
			}
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Summary", settings), summary, true);
        } else {
        	em.appendField(MessageManager.getMessage("Embed.Event.Pre.Summary", settings), "NOT SET", true);
		}
        if (event.getDescription() != null) {
			String description = event.getDescription();
			if (description.length() > 500) {
				description = description.substring(0, 500);
				description = description + " (continues on Google Calendar View)";
			}
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Description", settings), description, true);
        } else {
        	em.appendField(MessageManager.getMessage("Embed.Event.Pre.Description", settings), "NOT SET", true);
		}
        if (event.shouldRecur()) {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Recurrence", settings), event.getRecurrence().toHumanReadable(), true);
        } else {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Recurrence", settings), "N/a", true);
        }
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.StartDate", settings), getHumanReadableDate(event.getViewableStartDate(), settings, true), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.StartTime", settings), EventMessageFormatter.getHumanReadableTime(event.getViewableStartDate(), settings, true), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.EndDate", settings), getHumanReadableDate(event.getViewableEndDate(), settings, true), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.EndTime", settings), EventMessageFormatter.getHumanReadableTime(event.getViewableEndDate(), settings, true), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.TimeZone", settings), event.getTimeZone(), true);

        em.withFooterText(MessageManager.getMessage("Embed.Event.Pre.Key", settings));
        EventColor ec = event.getColor();
        em.withColor(ec.getR(), ec.getG(), ec.getB());

        return em.build();
    }

    /**
     * Gets an EmbedObject for the specified CreatorResponse.
     * @param ecr The CreatorResponse involved.
     * @return The EmbedObject for the CreatorResponse.
     */
    public static EmbedObject getEventConfirmationEmbed(EventCreatorResponse ecr, GuildSettings settings) {
    	EventData ed = DatabaseManager.getManager().getEventData(settings.getGuildID(), ecr.getEvent().getId());
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Confirm.Title", settings));
        if (ed.getImageLink() != null && ImageUtils.validate(ed.getImageLink())) {
        	em.withImage(ed.getImageLink());
		}
        em.appendField(MessageManager.getMessage("Embed.Event.Confirm.ID", settings), ecr.getEvent().getId(), false);
        em.appendField(MessageManager.getMessage("Embed.Event.Confirm.Date", settings), getHumanReadableDate(ecr.getEvent().getStart(), settings, false), false);
        em.withFooterText(MessageManager.getMessage("Embed.Event.Confirm.Footer", settings));
        em.withUrl(ecr.getEvent().getHtmlLink());
        try {
            EventColor ec = EventColor.fromId(Integer.valueOf(ecr.getEvent().getColorId()));
            em.withColor(ec.getR(), ec.getG(), ec.getB());
        } catch (Exception e) {
            //Color is null, ignore and add our default.
            em.withColor(56, 138, 237);
        }

        return em.build();
    }

    /**
     *  Gets a formatted date.
     * @param eventDateTime The object to get the date from.
     * @return A formatted date.
     */
    public static String getHumanReadableDate(@Nullable EventDateTime eventDateTime, GuildSettings settings, boolean preEvent) {
    	try {
			if (eventDateTime == null) {
				return "NOT SET";
			} else {
				//Get timezone
				CalendarData data = DatabaseManager.getManager().getMainCalendar(settings.getGuildID());

				String timezone;
				if (!preEvent) {
					if (settings.useExternalCalendar()) {
						timezone = CalendarAuth.getCalendarService(settings).calendars().get(data.getCalendarAddress()).execute().getTimeZone();
					} else {
						timezone = CalendarAuth.getCalendarService().calendars().get(data.getCalendarAddress()).execute().getTimeZone();
					}
				} else {
					timezone = "America/Chicago";
				}
				if (eventDateTime.getDateTime() != null) {
					long dateTime = eventDateTime.getDateTime().getValue();
					LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of(timezone));
					DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");

					return format.format(ldt);

				} else {
					long dateTime = eventDateTime.getDate().getValue();
					LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of(timezone));
					DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");

					return format.format(ldt);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.sendException(null, "Failed to format date", e, EventMessageFormatter.class);
			return "ERROR! Code: E001";
		}
    }

    /**
     * Gets a formatted time.
     * @param eventDateTime The object to get the time from.
     * @return A formatted time.
     */
    public static String getHumanReadableTime(@Nullable EventDateTime eventDateTime, GuildSettings settings, boolean preEvent) {
		try {
			if (eventDateTime == null) {
				return "NOT SET";
			} else {
				//Get timezone
				CalendarData data = DatabaseManager.getManager().getMainCalendar(settings.getGuildID());

				String timezone;
				if (!preEvent) {
					if (settings.useExternalCalendar()) {
						timezone = CalendarAuth.getCalendarService(settings).calendars().get(data.getCalendarAddress()).execute().getTimeZone();
					} else {
						timezone = CalendarAuth.getCalendarService().calendars().get(data.getCalendarAddress()).execute().getTimeZone();
					}
				} else {
					timezone = "America/Chicago";
				}
				if (eventDateTime.getDateTime() != null) {
					long dateTime = eventDateTime.getDateTime().getValue();
					LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of(timezone));
					DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm:ss a");

					return format.format(ldt);

				} else {
					long dateTime = eventDateTime.getDate().getValue();
					LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of(timezone));
					DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm:ss a");

					return format.format(ldt);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.sendException(null, "Failed to format date", e, EventMessageFormatter.class);
			return "ERROR! Code: E002";
		}
    }

    public static String getHumanReadableDateTime(@Nullable EventDateTime eventDateTime, GuildSettings settings, boolean preEvent) {
		try {
			if (eventDateTime == null) {
				return "NOT SET";
			} else {
				//Get timezone
				CalendarData data = DatabaseManager.getManager().getMainCalendar(settings.getGuildID());

				String timezone;
				if (!preEvent) {
					if (settings.useExternalCalendar()) {
						timezone = CalendarAuth.getCalendarService(settings).calendars().get(data.getCalendarAddress()).execute().getTimeZone();
					} else {
						timezone = CalendarAuth.getCalendarService().calendars().get(data.getCalendarAddress()).execute().getTimeZone();
					}
				} else {
					timezone = "America/Chicago";
				}
				if (eventDateTime.getDateTime() != null) {
					long dateTime = eventDateTime.getDateTime().getValue();
					LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of(timezone));
					DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a");

					return format.format(ldt);

				} else {
					long dateTime = eventDateTime.getDate().getValue();
					LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.of(timezone));
					DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a");

					return format.format(ldt);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.sendException(null, "Failed to format date", e, EventMessageFormatter.class);
			return "ERROR! Code: E003";
		}
	}
}