package com.example.demo.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.UUID;

@Service
public class GoogleCalendarService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarService.class);
    private final Calendar calendarService;

    public GoogleCalendarService() throws IOException, GeneralSecurityException {
        try {
            InputStream credentialsStream = GoogleCalendarService.class.getResourceAsStream("/credentials.json");
            if (credentialsStream == null) {
                logger.error("Credentials file not found at /resources/credentials.json. Ensure the file is in src/main/resources.");
                throw new IOException("Credentials file not found at /resources/credentials.json");
            }
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            this.calendarService = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("GreenwichGraduationProject")
                    .build();
            logger.info("Google Calendar Service initialized successfully.");
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Failed to initialize Google Calendar Service: {}", e.getMessage(), e);
            throw e;
        }
    }
    public String createMeetingEvent(String eventId) throws IOException {
        try {
            String safeEventId = (eventId == null || eventId.trim().isEmpty()) ? "room-" + UUID.randomUUID().toString() : eventId.trim();
            Event event = new Event()
                    .setSummary("Meeting for " + safeEventId)
                    .setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())))
                    .setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(
                            LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())))
                    .setConferenceData(new ConferenceData()
                            .setCreateRequest(new CreateConferenceRequest()
                                    .setRequestId(UUID.randomUUID().toString())));
            // Remove the ConferenceSolutionKey entirely

            event = calendarService.events().insert("primary", event)
                    .setConferenceDataVersion(1)
                    .execute();

            String meetLink = event.getHangoutLink();
            if (meetLink == null || meetLink.isEmpty()) {
                logger.error("Google Calendar API returned null or empty meet link for eventId: {}", safeEventId);
                throw new IOException("Failed to generate valid Google Meet link");
            }
            logger.info("Created Google Meet event with link: {} for eventId: {}", meetLink, safeEventId);
            return meetLink;
        } catch (IOException e) {
            logger.error("Failed to create Google Meet event for eventId: {}", eventId, e);
            throw e;
        }
    }
}