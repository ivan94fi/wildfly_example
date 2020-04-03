package mappers;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import daos.StructureDAO;
import domain.Booking;
import domain.Structure;
import dtos.BookingDTO;

@RequestScoped
public class BookingMapper {

    @Inject
    private StructureDAO structureDAO;

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
        Structure structure = structureDAO.findById(dto.getStructure());
        if (structure == null) {
            throw new IllegalArgumentException(
                    "The referenced structure does not exist, booking cannot be created.");
        }
        booking.setStructure(structure);
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
