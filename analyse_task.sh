#!/bin/bash

gawk 'BEGIN {
	# The demographic data.
	id = 0;
	name = "";
	age = 0;
	sex = "";
	is_student = "";
	hours_comp = 0;
	hours_docs = 0;
	doc_name = "";
	num_pages = 0
	condition = "";
	date_completed = "";
	
	# The measured data.
	page_count = 0; 
	num_trials = 0; 
	task_time = 0;
	num_visits = 0;
	
	# The current total times.
	current_scroll_total = 0;
	current_thumb_total = 0;
	current_total = 0;
      }
      
      $15 ~ /PageVisit/ {
	# Store the distances and the visits.
	trial_dist_arr[$23] += $26;
	trial_page_visits_arr[$23] += 1;
	
	# Determine the number of misses.
	trial_page_miss_arr[$23][trial_page_visits_arr[$23]-1] = $13;
      }
      
      $14 ~ /Thumbnail/ && $15 ~ /PageVisit/ {
	# Increase the number of visits via thumbnails. 
	trial_thumb_visit_arr[$23] += 1;
      }
      
      $14 ~ /Viewer/ && $15 ~ /PageVisit/ {
	# Determine the number of visits via scroll.
	trial_scroll_visit_arr[$23] += 1;
      }
      
      $15 ~ /PageFound/ {
	# Store the time to complete each trial.
	trial_time_arr[$23] = $24
	
	#Determine the scroll time, thumb interface time, and total time for each trial.
	trial_scroll_time_arr[$23] = $19 - current_scroll_total;
	trial_thumb_time_arr[$23] = $20 - current_thumb_total;
	trial_total_time_arr[$23] = $21 - current_total;
	current_scroll_total = $19;
	current_thumb_total = $20;
	current_total = $21;
	
	#Determine the task scroll and thumb interface time.
	task_scroll_time = $19;
	task_thumb_time = $20;
	task_total_time = $21;
	
	# Retrieve the demographic data.
	id = $1;
	name = $2;
	age = $3;
	sex = $4;
	is_student = $5;
	hours_comp = $6;
	hours_docs = $7;
	doc_name = $9;
	num_pages = $10;
	condition = $28;
	date_completed = $8;
	
	# Retrieve the measured data.
	page_count += $22; 
	num_trials = $23;
	task_time = $25;
	
	# Determine the target.
	trial_target_arr[$23] = $27;
      }
      
      $15 ~ /MouseMovement/ {
	trial_mouse_speed_arr[$23] += $12;
	trial_mouse_events_arr[$23] += 1;
      }
      
      $15 ~ /ScrollBar|MouseWheel/ {
	trial_scroll_speed_arr[$23] += $11;
	trial_scroll_events_arr[$23] += 1;
      }
      
      $15 ~ /SFTswitch/ {
	trial_SFT_switches_arr[$23] += 1;
      }
      
      END { 
	# Print the results of the demographic data.
	print "Pariticpant info --";
	print "\tID: " id ".";
	print "\tName: " name "."; 
	print "\tAge: " age ".";
	print "\tSex: " sex ".";
	print "\tStudent " is_student ".";
	print "\tHours on comp: " hours_comp ".";
	print "\tHours viewing docs: " hours_docs ".";
	print "\tDocument name: " doc_name ".";
	print "\tNumber of pages: " num_pages ".";
	print "\tCondition: " condition "."; 
	print "\tDate completed: " date_completed ".";
	print "--";
	
	# Print the results of the measured data. 
	print "Number of trials: " num_trials "."; 
	print "Average pages visited per tial: " page_count/num_trials "."; 
	
	trial_index = 1;
	task_distance = 0;
	task_page_visits = 0;
	
	task_mouse_speed = 0;
	task_mouse_events = 0;
	
	task_scroll_speed = 0;
	task_scroll_events = 0;
	
	task_thumb_visits = 0;
	task_scroll_visits = 0;
	
	task_SFT_switches = 0;
	
	# Print the header.
	print "Trial_Number\tTrial_Time\tTrial_Distance\tNumber_Pages_Visited\tAverage_Distance(Pages/visit)\tTotal_Mouse_Speed(p/ms)\tNumber_Of_Mouse_Events\tAverage_Mouse_Speed(p/ms/event)\tTotal_Scroll_Speed\tNumber_Of_Scroll_Events\tAverage_Scroll_Speed(p/ms/event)\tViewer_Time\tSFT_Time\tTotal_Time\tPages_Visited_Via_Thumbs\tPages_Visited_Via_Scrolling\tSFT_Switches\tTarget\tMisses";
	
	# Print the resuts of each trial.
	for (trial in trial_time_arr){
	  #Determine the mouse speed.
	  average_mouse_speed = "NA";
	  total_mouse_speed = 0;
	  trial_mouse_events = 0;
	  if (trial_mouse_events_arr[trial_index] != 0){
	    average_mouse_speed = trial_mouse_speed_arr[trial_index]/trial_mouse_events_arr[trial_index];
	    trial_mouse_events = trial_mouse_events_arr[trial_index];
	    total_mouse_speed = trial_mouse_speed_arr[trial_index];
	  }
	  
	  #Determine the scroll speed.
	  average_scroll_speed = "NA";
	  total_scroll_speed = 0;
	  trial_scroll_events = 0;
	  if (trial_scroll_events_arr[trial_index] != 0){
	    average_scroll_speed = trial_scroll_speed_arr[trial_index]/trial_scroll_events_arr[trial_index];
	    total_scroll_speed = trial_scroll_speed_arr[trial_index];
	    trial_scroll_events = trial_scroll_events_arr[trial_index];
	  }
	  
	  #Determine the number of page visits via thumbnail.
	  thumb_visits = 0;
	  scroll_visits = 0;
	  if (trial_thumb_visit_arr[trial_index] != 0){
	    thumb_visits = trial_thumb_visit_arr[trial_index];
	  }
	  if (trial_scroll_visit_arr[trial_index] != 0){
	    scroll_visits = trial_scroll_visit_arr[trial_index];
	  }
	  
	  #Determine the number of SFT switches. 
	  trial_SFT_switches = 0;
	  if (trial_SFT_switches_arr[trial_index] != 0){
	    trial_SFT_switches = trial_SFT_switches_arr[trial_index];
	  }
	
	  # Print the results for the trial.
	  printf(trial_index "\t" trial_time_arr[trial_index] "\t" trial_dist_arr[trial_index] "\t" trial_page_visits_arr[trial_index] "\t%.2f\t%.2f\t" trial_mouse_events "\t%.2f\t%.2f\t" trial_scroll_events "\t%.2f\t%.2f\t%.2f\t%.2f\t" thumb_visits "\t" scroll_visits "\t" trial_SFT_switches "\t" trial_target_arr[trial_index], trial_dist_arr[trial_index]/trial_page_visits_arr[trial_index], total_mouse_speed, average_mouse_speed, total_scroll_speed, average_scroll_speed, trial_scroll_time_arr[trial_index], trial_thumb_time_arr[trial_index], trial_total_time_arr[trial_index]);
	  
	  # Print the page misses. 
	  for (n = 0; n < trial_page_visits_arr[trial_index]; n += 1){
	    printf("\t" trial_page_miss_arr[trial_index][n]);
	  }
	  
	  # Start a new line.
	  printf("\n");
	  
	  # Determine the task results based on the individual trial results. 
	  task_distance += trial_dist_arr[trial_index];
	  task_page_visits += trial_page_visits_arr[trial_index];
	  
	  task_mouse_speed += total_mouse_speed;
	  task_mouse_events += trial_mouse_events;
	  
	  task_scroll_speed += total_scroll_speed;
	  task_scroll_events += trial_scroll_events;
	  
	  task_thumb_visits += thumb_visits;
	  task_scroll_visits += scroll_visits;
	  
	  task_SFT_switches += trial_SFT_switches;
	  
	  # Increase the trial index.
	  trial_index += 1;
	}
	
	# Determine mouse speed and scroll speed.
	average_mouse_speed = "NA";
	average_scroll_speed = "NA";
	if (task_mouse_events != 0){
	  average_mouse_speed = task_mouse_speed/task_mouse_events;
	}
	if (task_scroll_events != 0){
	  average_scroll_speed = task_scroll_speed/task_scroll_events;
	}
	
	# Print the results for the task.
	printf("Task" "\t" task_time "\t" task_distance "\t" task_page_visits "\t%.2f\t%.2f\t" task_mouse_events "\t%.2f\t%.2f\t" task_scroll_events "\t%.2f\t%.2f\t%.2f\t%.2f\t" task_thumb_visits "\t" task_scroll_visits "\t" task_SFT_switches, task_distance/task_page_visits, task_mouse_speed, average_mouse_speed, task_scroll_speed, average_scroll_speed, task_scroll_time, task_thumb_time, task_total_time);
  }' $1
      
exit 0