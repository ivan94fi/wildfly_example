package mappers;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import domain.Booking;
import dtos.BookingDTO;

public class BookingMapper {

    public BookingDTO convert(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("booking cannot be null");
        }
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setCreationDate(this.formatDate(booking.getCreationDate()));
        dto.setBookingStart(this.formatDate(booking.getBookingStart()));
        dto.setBookingEnd(this.formatDate(booking.getBookingEnd()));
        dto.setStructure(booking.getStructure().getId());
        return dto;
    }

    public void transfer(BookingDTO dto, Booking booking) {
        if (dto == null) {
            throw new IllegalArgumentException("dto cannot be null");
        }
        if (booking == null) {
            throw new IllegalArgumentException("booking cannot be null");
        }
        booking.setCreationDate(this.parseDate(dto.getCreationDate()));
        booking.setBookingStart(this.parseDate(dto.getBookingStart()));
        booking.setBookingEnd(this.parseDate(dto.getBookingEnd()));
    }

    private String formatDate(LocalDateTime date) {
        String formattedDate;
        try {
            formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("cannot format date");
        }
        return formattedDate;
    }

    private LocalDateTime parseDate(String dateString) {
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDateTime.parse(dateString);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("cannot parse date");
        }
        return parsedDate;
    }

}
