# Dynamic Floor Plan API Documentation

## Overview

The Dynamic Floor Plan API provides a flexible system for managing floor plan data using dynamically created tables. Each floor plan is stored in a separate table with a naming convention that follows the pattern: `{officeLocation}_{buildingName}_Floor{floorId}_table`.

### Table Naming Convention

Tables are automatically created with the following naming pattern:
```
{officeLocation}_{buildingName}_Floor{floorId}_table
```

**Important:** Spaces and special characters are automatically removed and replaced with underscores to ensure valid SQL table names.

**Examples:**
- `IN10_campus30_Floor8_table` (from "campus 30" → "campus30")
- `IN20_campus40_Floor5_table` (from "campus 40" → "campus40")
- `US01_mainbuilding_Floor2_table` (from "main building" → "mainbuilding")
- `IN10_campus_30_Floor_8_table` (if spaces are preserved as underscores)

### Table Schema

Each dynamic table follows this structure:
```sql
CREATE TABLE {table_name} (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    building_name NVARCHAR(255) NOT NULL,
    office_location NVARCHAR(255) NOT NULL,
    floor_id NVARCHAR(255) NOT NULL,
    plan_json NVARCHAR(MAX) NOT NULL,
    updated_at DATETIME DEFAULT GETDATE()
);
```

## API Endpoints

### Base URL
```
/api/v1/dynamic-floor-plans
```

### 1. Save/Update Floor Plan Data

**POST** `/api/v1/dynamic-floor-plans`

Creates or updates floor plan data in the dynamic table.

**Request Body:**
```json
{
    "office_location": "IN10",
    "building_name": "campus30",
    "floor_id": "Floor8",
    "plan_json": "{\"seats\":[...],\"deskAreas\":[...],\"layout\":{...}}"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Floor plan data saved successfully",
    "data": {
        "table_name": "IN10_campus30_Floor8_table"
    }
}
```

### 2. Get Floor Plan Data

**GET** `/api/v1/dynamic-floor-plans?officeLocation={office}&buildingName={building}&floorId={floor}`

Retrieves floor plan data from the dynamic table.

**Parameters:**
- `officeLocation` (required): Office location code
- `buildingName` (required): Building name
- `floorId` (required): Floor identifier

**Response:**
```json
{
    "success": true,
    "message": "Floor plan data retrieved successfully",
    "data": {
        "office_location": "IN10",
        "building_name": "campus30",
        "floor_id": "Floor8",
        "plan_json": "{\"seats\":[...],\"deskAreas\":[...],\"layout\":{...}}",
        "table_name": "IN10_campus30_Floor8_table"
    }
}
```

### 3. Check Floor Plan Existence

**GET** `/api/v1/dynamic-floor-plans/exists?officeLocation={office}&buildingName={building}&floorId={floor}`

Checks if a floor plan exists for the given parameters.

**Response:**
```json
{
    "success": true,
    "message": "Floor plan existence check completed",
    "data": {
        "exists": true,
        "office_location": "IN10",
        "building_name": "campus30",
        "floor_id": "Floor8",
        "table_name": "IN10_campus30_Floor8_table"
    }
}
```

### 4. Get Table Metadata

**GET** `/api/v1/dynamic-floor-plans/metadata?officeLocation={office}&buildingName={building}&floorId={floor}`

Retrieves metadata about the dynamic table.

**Response:**
```json
{
    "success": true,
    "message": "Table metadata retrieved successfully",
    "data": {
        "office_location": "IN10",
        "building_name": "campus30",
        "floor_id": "Floor8",
        "table_name": "IN10_campus30_Floor8_table",
        "metadata": {
            "record_count": 1,
            "last_updated": "2024-01-15T10:30:00"
        }
    }
}
```

### 5. Get All Office Locations

**GET** `/api/v1/dynamic-floor-plans/office-locations`

Retrieves all available office locations.

**Response:**
```json
{
    "success": true,
    "message": "Office locations retrieved successfully",
    "data": {
        "office_locations": ["IN10", "IN20", "US01"]
    }
}
```

### 6. Get Buildings by Office Location

**GET** `/api/v1/dynamic-floor-plans/buildings?officeLocation={office}`

Retrieves all buildings for a specific office location.

**Response:**
```json
{
    "success": true,
    "message": "Buildings retrieved successfully",
    "data": {
        "office_location": "IN10",
        "buildings": ["campus30", "campus35"]
    }
}
```

### 7. Get Floors by Office Location and Building

**GET** `/api/v1/dynamic-floor-plans/floors?officeLocation={office}&buildingName={building}`

Retrieves all floors for a specific office location and building.

**Response:**
```json
{
    "success": true,
    "message": "Floors retrieved successfully",
    "data": {
        "office_location": "IN10",
        "building_name": "campus30",
        "floors": ["Floor8", "Floor9", "Floor10"]
    }
}
```

### 8. Delete Floor Plan Table

**DELETE** `/api/v1/dynamic-floor-plans?officeLocation={office}&buildingName={building}&floorId={floor}`

Deletes the entire floor plan table and its data.

**Response:**
```json
{
    "success": true,
    "message": "Floor plan table deleted successfully",
    "data": {
        "office_location": "IN10",
        "building_name": "campus30",
        "floor_id": "Floor8",
        "table_name": "IN10_campus30_Floor8_table"
    }
}
```

## Floor Plan JSON Structure

The `plan_json` field should contain a JSON object with the following structure:

```json
{
    "seats": [
        {
            "id": "D1",
            "name": "Desk 1",
            "x": 7.0,
            "y": 5.0,
            "status": "available",
            "type": "desk",
            "equipment": ["Monitor", "Dock", "Window Seat"],
            "rotation": 0
        }
    ],
    "deskAreas": [
        {
            "id": "area1",
            "name": "Main Workspace",
            "x": 1.6101207354199758,
            "y": 1.869158965001005,
            "width": 60,
            "height": 40,
            "type": "workspace",
            "rotation": 0
        }
    ],
    "layout": {
        "width": 1200,
        "height": 800,
        "gridSize": 20
    }
}
```

### Seat Properties
- `id`: Unique identifier for the seat
- `name`: Display name for the seat
- `x`, `y`: Position coordinates
- `status`: Seat status ("available", "occupied", "reserved", "maintenance")
- `type`: Seat type ("desk", "meeting_room", etc.)
- `equipment`: Array of equipment available at the seat
- `rotation`: Rotation angle in degrees

### Desk Area Properties
- `id`: Unique identifier for the area
- `name`: Display name for the area
- `x`, `y`: Position coordinates
- `width`, `height`: Dimensions
- `type`: Area type ("workspace", "meeting_room", etc.)
- `rotation`: Rotation angle in degrees

### Layout Properties
- `width`: Total layout width
- `height`: Total layout height
- `gridSize`: Grid size for positioning

## Integration with Existing Floor Plan API

The existing floor plan endpoint `/api/v1/floor-plans` has been updated to work with the dynamic table system. It now:

1. Uses the dynamic table service to retrieve data
2. Parses the JSON data into the expected response format
3. Falls back to default data if no dynamic table exists

**Example Usage:**
```
GET /api/v1/floor-plans?building=campus30&office=IN10&floor=Floor8
```

This will automatically look for data in the `IN10_campus30_Floor8_table` and return the parsed floor plan response.

## Error Handling

All endpoints return consistent error responses:

```json
{
    "success": false,
    "message": "Error description"
}
```

Common HTTP status codes:
- `200`: Success
- `400`: Bad Request (validation errors)
- `404`: Not Found (floor plan doesn't exist)
- `500`: Internal Server Error

## Sample Data

The system automatically creates sample data for:
- `IN10_campus30_Floor8_table`
- `IN10_campus30_Floor9_table`
- `IN20_campus40_Floor5_table`

This sample data includes:
- 3 sample desks with different statuses and equipment
- 2 desk areas (Main Workspace and Quiet Zone)
- Layout configuration

## Security Considerations

- All endpoints are protected by JWT authentication
- Table names are validated to prevent SQL injection
- Input validation is performed on all parameters
- Access control can be implemented based on user roles

## Performance Notes

- Tables are created on-demand when first data is inserted
- Indexes are automatically created on the primary key
- Large JSON data is stored efficiently using NVARCHAR(MAX)
- Metadata queries are optimized for quick lookups 