#!/bin/bash

gawk 'BEGIN {
	# The demographic data.
	username = "";
	doc_name = "";
	num_pages = 0
	condition = "";
	date_completed = "";
	
	# The measured data.
	num_pages_visited = 0;
	
	total_mouse_speed = 0.0;
	total_mouse_events = 0;
	
	total_scroll_speed = 0.0;
	total_scroll_events = 0;
	
	viewer_time = 0;
	SFT_time = 0;
	total_time = 0;
	
	page_visits_via_thumbs = 0;
	page_visits_via_scroll = 0;
	
	SFT_switches = 0;
      }
      
      {
	# Obtain the demographic data. 
	username = $1;
	doc_name = $3;
	num_pages = $4;
	condition = $17;
	date_completed = $2;
	
	# Obtain the measured data. 
	num_pages_visited = $16;
	
	viewer_time = $13;
	SFT_time = $14;
	total_time = $15;
      }
      
      $9 ~ /MouseMovement/ {
	total_mouse_speed += $6;
	total_mouse_events += 1;
      }
      
      $9 ~ /ScrollBar|MouseWheel/ {
	total_scroll_speed += $5;
	total_scroll_events += 1;
      }
      
      $8 ~ /Thumbnail/ && $9 ~ /PageVisit/ {
	page_visits_via_thumbs += 1;
      }
      
      $8 ~ /Viewer/ && $9 ~ /PageVisit/ {
	page_visits_via_scroll += 1;
      }
      
      $9 ~ /SFTswitch/ {
	SFT_switches += 1;
      }
      
      END {
      # Print the results of the demographic data.
	print "Usage Data --";
	print "\tUsername: " username ".";
	print "\tDocument name: " doc_name ".";
	print "\tNumber of pages: " num_pages ".";
	print "\tCondition: " condition ".";
	print "\tDate completed: " date_completed ".";
	print "--";
	
	# Print the header.
	print "Number_Pages_Visited\tTotal_Mouse_Speed\tNumber_Of_Mouse_Events\tAverage_Mouse_Speed(p/ms/event)\tTotal_Scroll_Speed\tNumber_Of_Scroll_Events\tAverage_Scroll_Speed(p/ms/event)\tViewer_Time\tSFT_Time\tTotal_Time\tPages_Visited_Via_Thumbs\tPages_Visited_ViaScrolling\tSFT_Switches";
	
	# Determine the average mouse speed;
	average_mouse_speed = "NA";
	if (total_mouse_events != 0){
	  average_mouse_speed = total_mouse_speed/total_mouse_events;
	}
	
	# Determine the average scroll speed.
	average_scroll_speed = "NA";
	if (total_scroll_events != 0){
	  average_scroll_speed = total_scroll_speed/total_scroll_events;
	}
	
	#Print the results.
	printf(num_pages_visited "\t" total_mouse_speed "\t" total_mouse_events "\t%.2f\t" total_scroll_speed "\t" total_scroll_events "\t%.2f\t" viewer_time "\t" SFT_time "\t" total_time "\t" page_visits_via_thumbs "\t" page_visits_via_scroll "\t" SFT_switches "\n", average_mouse_speed, average_scroll_speed);
      }' $1;
      
exit 0;