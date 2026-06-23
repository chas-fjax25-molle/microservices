package com.example.booking.feature.booking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.BookingRegistrationDTO;
import com.example.common.dto.BookingResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/booking-service/bookings")
@Tag(name = "Booking Service", description = "Endpoints for managing bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Create a new booking", description = "Create a new booking for an event. Requires USER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO createBooking(@RequestBody @Valid BookingRegistrationDTO booking) {
        return bookingService.createBooking(booking);
    }

    @Operation(summary = "Get booking by ID", description = "Retrieve a booking by its ID. Users can only retrieve their own bookings; admins can retrieve any booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions to access this booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDTO getBookingById(@PathVariable @NotNull UUID id) {
        return bookingService.getBookingById(id);
    }

    @Operation(summary = "Get all bookings", description = "Retrieve a list of all bookings. Requires ADMIN role only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can retrieve all bookings"),
            @ApiResponse(responseCode = "404", description = "Bookings not found")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDTO> getBookings() {
        return bookingService.getAllBookings();
    }

    @Operation(summary = "Get bookings by user ID", description = "Retrieve a list of bookings for a specific user. Users can only retrieve their own bookings; admins can retrieve any user's bookings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot retrieve bookings for other users unless ADMIN"),
            @ApiResponse(responseCode = "404", description = "User or bookings not found")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDTO> getBookings(@PathVariable @NotNull UUID userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @Operation(summary = "Update a booking", description = "Update an existing booking by its ID. Users can only update their own bookings; admins can update any booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking ID or input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot update bookings for other users unless ADMIN"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDTO updateBooking(@PathVariable @NotNull UUID id,
            @RequestBody @Valid BookingRegistrationDTO update) {
        return bookingService.update(id, update);
    }

    @Operation(summary = "Delete a booking", description = "Delete an existing booking by its ID. Users can only delete their own bookings; admins can delete any booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete bookings for other users unless ADMIN"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookingById(@PathVariable @NotNull UUID id) {
        bookingService.delete(id);
    }
}
