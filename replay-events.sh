#!/bin/bash

# Target the processing group name we defined in CurrentAccountViewProjection.java
PROCESSING_GROUP="current-account-view"
URL="http://localhost:8080/api/admin/replay/$PROCESSING_GROUP"

echo "Initiating Event Replay for: $PROCESSING_GROUP..."
echo "Sending POST request to $URL"

# Send the API request using curl
curl -X POST $URL

echo -e "\n\nReplay command sent successfully! Check your application logs to monitor the rebuild."