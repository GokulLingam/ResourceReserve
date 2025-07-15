-- Booking table schema for SQL Server
-- This script creates the bookings table with proper data types

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='bookings' AND xtype='U')
CREATE TABLE bookings (
    id VARCHAR(255) PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    book_type VARCHAR(50) NOT NULL,
    sub_type VARCHAR(255),
    office_location VARCHAR(255),
    building VARCHAR(255),
    floor VARCHAR(255),
    recurrence_type VARCHAR(50),
    end_date DATE,
    status VARCHAR(50),
    user_id VARCHAR(255),
    notes TEXT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

-- Create index for better performance on common queries
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_bookings_date_subtype')
CREATE INDEX idx_bookings_date_subtype ON bookings(date, sub_type);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_bookings_user_id')
CREATE INDEX idx_bookings_user_id ON bookings(user_id);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_bookings_location')
CREATE INDEX idx_bookings_location ON bookings(office_location, building, floor, date);

-- Create the custom dates table for recurring bookings
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='booking_custom_dates' AND xtype='U')
CREATE TABLE booking_custom_dates (
    booking_id VARCHAR(255) NOT NULL,
    custom_date DATE NOT NULL,
    PRIMARY KEY (booking_id, custom_date),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
); 